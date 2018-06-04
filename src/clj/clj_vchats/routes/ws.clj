(ns clj-vchats.routes.ws
  (:require [org.httpkit.server :refer [send! with-channel on-close on-receive]]
            [clj-time.local :as l]
            [clojure.data.json :as json]
            [clj-vchats.db.core :as db]))

(def channels (atom {}))

(defn connect! [channel channel-name]
  (println "open channel" channel-name)
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
  (let [mmsg (json/read-str msg :key-fn keyword)]
   (doseq [channel ((keyword channel-name) @channels)]
     (do
       ;; (println "send! to " channel-name)
       (if (db/get-channel {:chan_name channel-name})
         (if (= "message" (:type mmsg))
           (do ;; (println "saved" mmsg)
               (db/save-message! (-> mmsg (dissoc :type)
                                     (assoc :chan_name channel-name)
                                     (assoc :ltime (clj-time.local/local-now))))
               (send! channel msg))
           (do (println "non saved" mmsg)
               (send! channel msg)))
         (send! channel (json/write-str {:type "channel was closed"})))))))

(defn ws-handler [req]
  (with-channel req channel
    (let [channel-name (-> req :parameters :path :id)]
      (connect! channel channel-name)
      (on-close channel #(disconnect! channel channel-name %))
      (on-receive channel #(notify-clients channel-name %)))))
