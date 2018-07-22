(ns clj-vchats.db.core
  (:require
    [cheshire.core :refer [generate-string parse-string]]
    [clj-time.jdbc]
    [clojure.java.jdbc :as jdbc]
    [clojure.tools.logging :as log]
    [conman.core :as conman]
    [clj-vchats.config :refer [env]]
    [mount.core :refer [defstate]])
  (:import org.postgresql.util.PGobject
           java.sql.Array
           clojure.lang.IPersistentMap
           clojure.lang.IPersistentVector
           [java.sql
            BatchUpdateException
            PreparedStatement]))

(defstate ^:dynamic *db*
  :start (if-let [jdbc-url (env :database-url)]
           (conman/connect! {:jdbc-url jdbc-url})
           (do
             (log/warn "database connection URL was not found, please set :database-url in your config, e.g: dev-config.edn")
             *db*))
  :stop (conman/disconnect! *db*))

(conman/bind-connection *db* "sql/queries.sql")

(extend-protocol jdbc/IResultSetReadColumn
  Array
  (result-set-read-column [v _ _] (vec (.getArray v)))

  PGobject
  (result-set-read-column [pgobj _metadata _index]
    (let [type  (.getType pgobj)
          value (.getValue pgobj)]
      (case type
        "json" (parse-string value true)
        "jsonb" (parse-string value true)
        "citext" (str value)
        value))))

(defn to-pg-json [value]
      (doto (PGobject.)
            (.setType "jsonb")
            (.setValue (generate-string value))))

(extend-type clojure.lang.IPersistentVector
  jdbc/ISQLParameter
  (set-parameter [v ^java.sql.PreparedStatement stmt ^long idx]
    (let [conn      (.getConnection stmt)
          meta      (.getParameterMetaData stmt)
          type-name (.getParameterTypeName meta idx)]
      (if-let [elem-type (when (= (first type-name) \_) (apply str (rest type-name)))]
        (.setObject stmt idx (.createArrayOf conn elem-type (to-array v)))
        (.setObject stmt idx (to-pg-json v))))))

(extend-protocol jdbc/ISQLValue
  IPersistentMap
  (sql-value [value] (to-pg-json value))
  IPersistentVector
  (sql-value [value] (to-pg-json value)))




;; test ;;;;;;;;;;;;;;;;;;;;;
;; (try (create-user!
;;        {:name "elect" :pass "Meguru" :mail "e.tmailbank@gmail.com"})
;;      (catch Exception e {:error "cannot create user"}))
;; ==> (:error res) => nil : success insert
;;
;; (get-user {:name "elect"})
;; nil => not found
;;
;;(delete-user! {:name "elect"})
;; (get-users)
;;
;; (try (create-user!
;;        {:name "Mokke" :pass "Meguru" :mail "e.tmailbank@gmail.com"})
;;      (catch Exception e {:error "cannot create user"}))
;;(get-user {:name "Mokke"})


;; (try (create-channel! {:chan_name "elect" :master_name "elect"})
;;      (catch Exception e {:error "cannot create channel"}))
;; (get-channels)

;; (get-channel {:chan_name "elect"})
;; (:master_name (get-channel {:chan_name "elect"}))
;;
;; (try (create-channel! {:chan_name "Mokke" :master_name "Mokke"})
;;      (catch Exception e {:error "cannot create channel"}))
;; (map :chan_name (get-channels))
;; (delete-channel! {:chan_name "           "})

;; (save-message! {:chan_name "elect"
;;                 :name "elect"
;;                 :params  "Hello"
;;                 :ltime (clj-time.local/local-now)})
;; (get-messages)

;; (reverse (map #(assoc % :ltime (clj-time.coerce/to-long (:ltime %)))
;;               (map #(clojure.set/rename-keys % {:user_name :name
;;                                                 :messages :params})
;;                    (get-message {:chan_name "elect"}))))

;; (delete-message! {:chan_name "elect"})

;; (get-channels)

;; (let [invite-name "elect"
;;       chan-name "testchannel"]
;;   (if (:inviter_name (get-inviter {:chan_name chan-name}))
;;     (str "Already Exist")
;;     (if
;;        (zero? (set-inviter! {:inviter_name "elect"
;;                              :chan_name chan-name}))
;;      (str "Invalid User")
;;      (str "Yes"))))

;; (delete-inviter! {:chan_name "testchannel"})

;; (delete-channel! {:chan_name "hogehoge"})

;; (get-inviter {:chan_name "testchannel"})
;; (get-channel {:chan_name "testchannel"})

;; (get-user {:name "elec"})

;; (zero?
;;  (set-inviter! {:inviter_name "elect"
;;                 :chan_name "testchannel"}))
;; (get-channels)

;; (get-inviter {:chan_name "testchannel"})
;; (:master_name (get-channel {:chan_name "111111"}))
;; (:inviter_name (get-channel {:chan_name "111111"}))

;; (get-user {:name "testuser"}) ;;=> testpassword

;; (get-channel {:chan_name "testchannel"})

;; (get-channel {:chan_name "test"})
;; (delete-inviter! {:chan_name "test"})
;; (get-skin {:chan_name "test"})
;; => {:master_face 0, :master_bg 0, :inviter_face 0, :inviter_bg 0}

;; (get-inviter {:chan_name "test"})

