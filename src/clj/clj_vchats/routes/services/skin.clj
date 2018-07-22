(ns clj-vchats.routes.services.skin
  (:require [clj-vchats.db.core :as db]))

(defn save-slave-skin [uname k v channel-name]
  (let [iname (:inviter_name (db/get-inviter {:chan_name channel-name}))]
    (if (and (int? v) (= iname uname))
      (do
        (if (= k "BG")
          (db/set-inviter-bg! {:chan_name channel-name :inviter_bg v})
          (db/set-inviter-face! {:chan_name channel-name :inviter_face v}))
        true)
      false)))

(defn save-master-skin [uname k v channel-name]
  (let [iname (:master_name (db/get-master {:chan_name channel-name}))]
    (if (and (int? v) (= iname uname))
      (do
        (if (= k "BG")
          (db/set-master-bg! {:chan_name channel-name :master_bg v})
          (db/set-master-face! {:chan_name channel-name :master_face v}))
        true)
      false)))

(defn get-skins [chan]
  (if (db/get-channel {:chan_name chan})
    {:status 200
     :body (db/get-skin {:chan_name chan})}
    {:status 404
     :bdoy {:message "not found"}}))


 (db/get-skin {:chan_name "test"})
;; (db/get-channel {:chan_name "test"})


