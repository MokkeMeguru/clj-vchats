(ns clj-vchats.routes.ws
  (:require [org.httpkit.server :refer [send! with-channel on-close on-receive]]
            [clj-time.local :as l]))

(def channels (atom {}))

(defn connect! [channel channel-name]
  (println "open channel")
  (swap! channels
         (fn [chans] (assoc chans
                            (keyword channel-name)
                            (cons channel ((keyword channel-name) chans))))))

(defn disconnect! [channel channel-name status]
  (println "channel close")
  (swap! channels
         (fn [chans] (assoc chans
                            (keyword channel-name)
                            (remove #(= channel %) ((keyword channel-name) chans))))))

(defn notify-clients [channel-name msg]
  (doseq [channel ((keyword channel-name) @channels)]
    (do
      (println "send!" channel)
      (send! channel msg))))

(defn ws-handler [req]
  (with-channel req channel
    (let [channel-name (-> req :parameters :path :id)
          _ (println  "Channel" channel)]
      (connect! channel channel-name)
      (on-close channel #(disconnect! channel channel-name %))
      (on-receive channel #(notify-clients channel-name %)))))
