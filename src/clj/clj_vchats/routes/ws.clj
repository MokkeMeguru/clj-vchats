(ns clj-vchats.routes.ws
  (:require [org.httpkit.server :refer [send! with-channel on-close on-receive]]
            [clj-time.local :as l]
            [clojure.data.json :as json]
            [clj-vchats.db.core :as db]
            [clj-vchats.routes.services.closing :as closing]
            [clj-vchats.routes.services.invite :as invite]
            [clj-vchats.routes.services.skin :as skin]
            [clj-vchats.routes.services.audio :refer [get-audio-b64]]))

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
  (let [mmsg (json/read-str msg :key-fn keyword)
        in-channels ((keyword channel-name) @channels)]
    (if (db/get-channel {:chan_name channel-name})
      (condp = (:type mmsg)
        "close"
        (let [authorization (:params mmsg)]
          (when (closing/check-close-key channel-name authorization)
            (do
              (closing/close-channel channel-name)
              (doseq [channel in-channels]
               (send! channel
                      (json/write-str {:type "close" :params (str "This channel will be close by " (:name mmsg))}))))))
        "message"
        (do
          (print "message:" mmsg)
          (db/save-message! (-> mmsg (dissoc :type)
                                (assoc :chan_name channel-name)
                                (assoc :ltime (clj-time.local/local-now))))
          (doseq [channel in-channels]
            (do
              (print channel msg "\n")
              (send! channel msg))))
        "invite"
        (do
          (print "invite" mmsg)
          (let [uname (:name mmsg)
               params (:params mmsg)
               key (:key params)
               iname (:name params)]
           (when (invite/check-invite-key channel-name key)
             (when (invite/invite-channel channel-name iname)
               (doseq [channel in-channels]
                 (send! channel (json/write-str {:type "invite"
                                                 :params iname})))))))
        "accept"
        (do (print mmsg)
            (let [uname (:name mmsg)]
              (print uname)
           (when (invite/accept-invite channel-name uname)
             (doseq [channel in-channels]
               (send! channel msg)))
           ))
        "talk"
        (let [uname (:name mmsg)
              params (:params mmsg)
              text (:text params)
              voice (if (:voice params) (:voice params) "sumire")
              rate (if (and (:rate params) (> (:rate params) 0.0) (> 2.0 (:rate params))) (:rate params) 1.0)
              pitch (if (and (:pitch params) (> (:pitch params) 0.0) (> 2.0 (:rate params))) (:pitch params) 1.0)]
          (print mmsg)
          ;; TODO: check uname in (channel's master_name invite_name)
          (if text
            (let [b64-voice (get-audio-b64 text :voice voice :rate rate :pitch pitch)] ;; TODO: check validation
              (doseq [channel in-channels]
                (send! channel (json/write-str
                                {:type "voice-data"
                                 :name uname
                                 :params b64-voice}))))))
        "slave-skin"
        (let [params (:params mmsg)
              uname (:name mmsg)
              k (:key params)
              v (:value params)]
          ;; save slave skin
          (when (skin/save-slave-skin uname k v channel-name)
            (doseq [channel in-channels] (send! channel msg))))
        "master-skin"
        (let [params (:params mmsg)
              uname (:name mmsg)
              k (:key params)
              v (:value params)]
          ;; save master skin
          (when (skin/save-master-skin uname k v channel-name)
            (doseq [channel in-channels] (send! channel msg))))
        (doseq [channel in-channels] (send! channel msg)))
      (doseq [channel in-channels]
        (send! channel (json/write-str {:type "error"
                                        :params "channel was closed"}))))))
(defn ws-handler [req]
  (with-channel req channel
    (let [channel-name (-> req :parameters :path :id)]
      (connect! channel channel-name)
      (on-close channel #(disconnect! channel channel-name %))
      (on-receive channel #(notify-clients channel-name %)))))

;; get-audio-b64 [text & {:keys [voice rate pitch] :or {voice "sumire" rate 1.0 pitch 1.0}}]


;; (json/write-str {:a {:b 2}}
;;                 )
;; (json/read-str (json/write-str {:a {:b 2}}
;;                                ))
;; memo
;; in #notify-clients, talk requires...
;; {"type" : "talk,
;;  "name" : name
;;  "params" :{"text : text,
;;            "voice" : voice,
;;            "rate" : rate,
;;            "pitch" : pitch}}


;; (if (< 2 (count "")) true false)
;; (json/read-str "{}")
