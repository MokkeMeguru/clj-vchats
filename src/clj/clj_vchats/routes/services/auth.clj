(ns clj-vchats.routes.services.auth
  (:require [clj-vchats.db.core :as db]
            [ring.util.http-response :as response]
            [ring.util.response :refer [redirect]]
            [clojure.tools.logging :as log]))

(defn resister!
  "if this name is already in use => false
  else => true"
  [user]
  (let [result (try (db/create-user! user)
                    (catch Exception e -1))]
    (if (= -1 result)
      false
      true)))

;; (resister! {:session 1} {:name "elect" :pass "mokke" :mail "e.tmailbank@gmail.com"})

;; (db/get-user {:name "elect"})
(defn login! [iname ipass]
  (when-let [{:keys [password]} (db/get-user {:name iname})]
    (when (= ipass password)
      true)))

(defn unauthorized-handler
  [req _]
  (println "unauthorized!")
  (if (buddy.auth/authenticated? req)
    (redirect "/")
    (redirect (format "/login?next=%s" (:uri req)))))
