(ns clj-vchats.routes.services.invite
  (:require [clj-vchats.routes.services.closing :refer [random-str]]
            [clj-vchats.db.core :as db]))

(def inviting (atom {})) ;; {chan_name one-time-key}

;; (def invite (atom {})) ;; {chan_name [name name name ...]}

;; https ----------------------------------------------
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
  "if (check-invite-key - -), he can invite him in the channel
  recommend:
  if this function had run,
  send the message {:type invite :paramas {:key key :name his-name})
    for this channel's users
  chan-name invite-user
  task : check invite-key
       : check invite-user is correct user
       : check invite-user isn't same as master-user
       : check not setting someone on the table
       : set invite-user in 'inviting
  :return boolean"
  [chan-name invite-user]
  (if (and
       (not (nil? (db/get-user {:name invite-user})))
       (nil? (:inviter_name (db/get-channel {:chan_name chan-name})))
       (not= invite-user (:master_name (db/get-channel {:chan_name chan-name}))))
    (do
      (swap! inviting assoc chan-name invite-user)
      true)
    false))

(defn accept-invite
  "if this function had run,
  send the message {:type accept :params his-name} for this channel's users
  task : check having his name in 'inviting
       : set his name on the table
  :return boolean"
  [chan-name invite-user]
  (let [s (get @inviting chan-name)]
    (if (= s invite-user)
      (do
        (db/set-inviter! {:inviter_name invite-user
                          :chan_name chan-name})
        (swap! inviting dissoc! chan-name)
        true)
      false)))
;; -----------------------------------------------------

;; (:inviter_name (get-channel {:chan_name "111111"})) => check no user
;; (db/get-user {:name "elect"}) => some information
;; (db/get-user {:name "hoge"})  => nil

;; (:master_name (db/get-channel {:chan_name "111111"})) => check not being same


