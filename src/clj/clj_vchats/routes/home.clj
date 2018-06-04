(ns clj-vchats.routes.home
  (:require [clj-vchats.layout :as layout]
            [clj-vchats.db.core :as db]
            [clojure.java.io :as io]
            [clj-vchats.middleware :as middleware]
            [clj-vchats.layout :refer [error-page]] ;;
            [ring.util.http-response :as response]
            [reitit.ring :as ring]
            [ring.middleware.session.cookie]
            [ring.middleware.session :refer [wrap-session]]
            ;; added!!!

            [clj-vchats.routes.services.auth :as auth]
            [reitit.coercion.spec :as rcs]))

(defn home-page [_]
  (layout/render "home.html"))

(defn login-page [req]
  (if-let [message (:error-message req)]
    (layout/render "login.html" {:error-message message})
    (if-let [message (-> req :query-params (get "message"))]
      (layout/render "login.html" {:error-message "Please Login"})
      (layout/render "login.html"))))

(defn resister-page [req]
  (if-let [message (-> req :query-params (get "message"))]
    (layout/render "resister.html" {:error-message "That name is already used"})
    (layout/render "resister.html")))

(defn channels-page [req]
  (if-let [message (-> req :query-params (get "message"))]
    (if (= message "nexist")
      (layout/render "channels.html" {:error-message "That channel isn't exist"})
      (layout/render "channels.html" {:error-message "This channel is already exist"}))
    (layout/render "channels.html")))

(defn channel-page [_]
  (layout/render "channel.html"))

(defn home-routes [] ;; add login :user/channels :channel-name/:user
  [["/" {:get {:handler    home-page
               :middleware [middleware/wrap-csrf
                            middleware/wrap-formats]}}]
   ["/ws" {:get {:handler (fn [_]
                            (-> (response/ok (-> "templates/test.html" io/resource slurp))
                                (response/header "Content-Type" "text/html; charset=utf-8")))}}]
   ["/docs" {:get {:handler (fn [_]
                              (-> (response/ok (-> "docs/docs.md" io/resource slurp))
                                  (response/header "Content-Type" "text/plain; charset=utf-8")))}}]
   ["/resister" {:get {:handler (fn [req] (resister-page req))}
                 :post {:handler (fn [req]
                                   (let [uname (-> req :form-params (get "username"))
                                         pass (-> req :form-params (get "password"))
                                         mail (-> req :form-params (get "mail"))]
                                     (if (filter false? (map #(> (count %) 54) [uname pass mail]))
                                       (if-not (db/get-user {:name uname})
                                         (try
                                           (do
                                             (db/create-user! {:name uname
                                                               :pass pass
                                                               :mail mail})
                                             (-> req
                                                 (merge (ring.util.response/redirect "/channels"))
                                                 (assoc-in [:cookies] {:identity {:value uname, :secure true, :max-age (* 3 3600)}})))
                                           (catch Exception e (do (ring.util.response/redirect "/resister?message=cmu"))))
                                         (ring.util.response/redirect "/resister?message=aexist"))
                                       (ring.util.response/redirect "/resister?message=invp") ;; with error message
                                       )))
                        :middleware [ring.middleware.cookies/wrap-cookies
                                     middleware/wrap-formats]}}]

   ["/login" {:get {:handler login-page
                    :middleware [middleware/wrap-csrf]}
              :post {:handler (fn [req]
                                (let [uname (-> req :form-params (get "username"))
                                      pass (-> req :form-params (get "password"))]
                                  (if (auth/login! uname pass)
                                    (-> req
                                        (merge (ring.util.response/redirect "/channels"))
                                        (assoc-in [:cookies] {:identity uname}))
                                    (login-page
                                     {:error-message "invalid user infomation"}))))
                     :middleware [ring.middleware.cookies/wrap-cookies
                                  ;; middleware/wrap-csrf
                                  middleware/wrap-formats]}}]
   ["/channels" {:get {:handler (fn [req] (let [name (:value (get (:cookies req) "identity"))]
                                            (if (and name (db/get-user {:name name}))
                                              (channels-page req)
                                              (ring.util.response/redirect "/login?message=plogin"))))
                       :middleware [ring.middleware.cookies/wrap-cookies
                                    middleware/wrap-csrf]}
                 :post {:parameters {:form-params {:chan_name string?}}
                        :handler (fn [req]
                                   (let [chan_name (-> req  :form-params (get "chan_name"))
                                         len (count chan_name)
                                         uname (:value (get (:cookies req) "identity"))]
                                     (if (and (> len 5)
                                              (db/get-user {:name uname})
                                              (not (db/get-channel {:chan_name chan_name})))
                                       (do
                                         (db/create-channel! {:chan_name chan_name
                                                              :master_name uname})
                                         (ring.util.response/redirect (str "/channel/" chan_name)))
                                       (ring.util.response/redirect "/channels?message=mcexception"))))
                        :middleware [ring.middleware.cookies/wrap-cookies
                                     middleware/wrap-formats]}}]
   ["/channel/:cname" {:get {:coercion rcs/coercion
                             :parameters {:path {:cname string?}}
                             :handler (fn [req] (let [uname (:value
                                                            (get (:cookies req) "identity"))
                                                      chan_name (-> req :parameters :path :cname)]
                                                  (if (db/get-user {:name uname})
                                                    (if (db/get-channel {:chan_name chan_name})
                                                      (channel-page req)
                                                      (ring.util.response/redirect "/channels?message=nexist")) ;; add erro message
                                                    (ring.util.response/redirect "/login?message=plogin"))))
                               :middleware [ring.middleware.cookies/wrap-cookies
                                            middleware/wrap-csrf]}}]

   ["/logout" {:get {:handler (fn [req]
                                (let [newreq (-> req
                                       (merge (ring.util.response/redirect "/login"))
                                       (assoc :cookies {:identity ""}))]
                                  newreq))
                     :middleware [middleware/wrap-csrf
                                  ring.middleware.cookies/wrap-cookies]}}]

   ["/admin" {:get {:handler (fn [req] (println "admin" req ) (home-page req))
                    :middleware [middleware/wrap-csrf]}}]
   ["/door" {:get {:handler #(-> % (merge (ring.util.response/redirect "/room"))
                                 (assoc-in [:cookies] {:identity "mokke"}))
                   :middleware [ring.middleware.cookies/wrap-cookies]}}]
   ["/room" {:get {:handler (fn [req]
                              (if (= "mokke" (:value (get (:cookies req) "identity")))
                                (home-page req)
                                (error-page {:status 500})))
                   :middleware [ring.middleware.cookies/wrap-cookies]}}]
   ["/user-page"
    ["/logout" {:get {:handler (fn [_]
                                 (-> (ring.util.response/redirect "/user-page/channels")
                                     (assoc-in [:cookies] {:login false})))
                      :middleware [middleware/wrap-csrf]}}]
    ["/channels" {:get {:handler (fn [req]
                                   (if (get-in req [:cookies] {:login true})
                                     (-> (channels-page)
                                         (assoc-in [:cookies]
                                                   (:cookies req)))
                                     (middleware/on-error req {})))
                        :middleware [middleware/wrap-csrf]}}]
    ["/channel/:cname" {:get {:coercion rcs/coercion
                              :parameters {:path {:cname string?}}
                              :handler channel-page
                              :middleware [ring.middleware.cookies/wrap-cookies
                                           middleware/wrap-csrf]}}]]])


