(ns clj-vchats.routes.services.invite
  (:require [clj-vchats.routes.services.closing :refer [random-str]]
            [clj-vchats.db.core :as db]))

(def inviting (atom {})) ;; {chan_name one-time-key}
(def invite (atom {})) ;; {chan_name [name name name ...]}

;; http ----------------------------------------------
(defn create-invite-key
  "for invite people request via http
  :args channel's-name master-user-name
  :return {:authorized boolean :key key} (key is optional)"
  [chan-name name]
  (if (and
       (nil? (:inviter_name (db/get-inviter {:chan_name chan-name})))
       (= name (:master_name (db/get-channel {:chan_name chan-name}))))
    (let [s (random-str)]
      (swap! inviting assoc chan-name s)
      (ring.util.http-response/ok s))
    (ring.util.http-response/not-acceptable "error")))

;; -----------------------------------------------------

;; websocket -------------------------------------------
(defn check-invite-key
  "for invite-channel's request via websocket
  :args {:authorized boolean :key key} (key is optional)
  (authorization-key is created by #create-close-key)
  :return boolean"
  [chan-name authorization]
  (let [s (get @inviting chan-name)]
    (if (= s authorization)
      (do (swap! inviting dissoc chan-name)
          true)
      false)))

(defn invite-channel
  "if (check-invite-key - -), he can invite in the channel
  recommend:
  if this function had run,
  send the message {:type invite :paramas ...) for this channel's users
  chan-name invite-user"
  [chan-name invite-user])
;; -----------------------------------------------------
