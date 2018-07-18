(ns clj-vchats.routes.services.closing
  (:require [clj-vchats.db.core :as db]))

(def closing (atom {})) ;; {chan_name one-time-key}

(defn random-str []
  (apply str (take 50 (repeatedly #(char (+ (rand 26) 65))))))

;; (random-str) this is the one-time-key

;; http -----------------------------------------------------
(defn create-close-key
  "for close-channel's request via http
  :args channel's-name user-name
  :return {:authorized boolean :key key} (key is optional)"
  [chan-name name]
  (if-not (= name (:master_name (db/get-channel {:chan_name chan-name})))
    (ring.util.http-response/not-acceptable "error")
    (let [s (random-str)]
      (swap! closing assoc chan-name s)
      (ring.util.http-response/ok s))))
;; ----------------------------------------------------------

;; websocket ------------------------------------------------
(defn check-close-key
  "for close-channel's request via websocket
  :args channel's-name authorization-key
  (authorization-key is created by #create-close-key)
  :return boolean"
  [chan-name authorization]
  (let [s (get @closing chan-name)]
    (if (= s authorization)
      (do (swap! closing dissoc chan-name)
          true)
      false)))

(defn close-channel
  "if (check-close-key - -), he can close the channel
  recommend:
  if this function had run,
  send the message {:type close (message)} for this channel's users"
  [chan-name]
  (db/delete-message! {:chan_name chan-name})
  (db/delete-channel! {:chan_name chan-name}))


;; (check-close-key "elect" "hoge")
;;(swap! closing assoc "elect" "hoge")
;; (get @closing "elect")
;; (swap! closing dissoc "elect")
