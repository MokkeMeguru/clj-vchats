(ns clj-vchats.handler
  (:require 
   [clj-vchats.layout :refer [error-page]]
   ;; [clj-vchats.routes.home :refer [home-routes]]
   [clj-vchats.routes.services :refer [service-routes]]
   [reitit.ring :as ring]
   [ring.util.http-response :as response]
   [ring.middleware.content-type :refer [wrap-content-type]]
   [ring.middleware.webjars :refer [wrap-webjars]]
   [clj-vchats.env :refer [defaults]]
   [buddy.auth.backends.session :refer [session-backend]] ;; !
   [buddy.auth.middleware :refer [wrap-authentication
                                  wrap-authorization]] ;; !
   [buddy.auth.accessrules :refer [wrap-access-rules]] ;; !
   [buddy.auth :refer [authenticated?]]
   [mount.core :as mount]
   [reitit.swagger-ui :as ui] ;; added!!!
   [clj-vchats.middleware :as middleware]
   [clj-vchats.routes.services.auth :as auth]))

(mount/defstate init-app
  :start ((or (:init defaults) identity))
  :stop  ((or (:stop defaults) identity)))

(mount/defstate app
  :start
  (ring/ring-handler
   (service-routes)
   ;; (ring/router
   ;;  (home-routes)
   ;;   {:data {:middleware [middleware/wrap-base]}})
   (ring/routes
    (ui/create-swagger-ui-handler {:url  "/api/swagger.json"
                                   :path "/swagger-ui"}) ;; Add!!!
    (ring/create-resource-handler {:path "/"})
    (wrap-content-type (wrap-webjars (constantly nil)))
    (ring/create-default-handler
     {:not-found
      (constantly (error-page {:status 404, :title "404 - Page not found"}))
      :method-not-allowed
      (constantly (error-page {:status 405, :title "405 - Not allowed"}))
      :not-acceptable
      (constantly (error-page {:status 406, :title "406 - Not acceptable"}))}))))

;; (mount/defstate app
;;   :start
;;   (ring/ring-handler
;;     (ring/router
;;       (home-routes)
;;       {:data {:middleware [middleware/wrap-base]}})
;;     (ring/routes
;;       (ring/create-resource-handler {:path "/"})
;;       (wrap-content-type (wrap-webjars (constantly nil)))
;;       (ring/create-default-handler
;;         {:not-found
;;          (constantly (error-page {:status 404, :title "404 - Page not found"}))
;;          :method-not-allowed
;;          (constantly (error-page {:status 405, :title "405 - Not allowed"}))
;;          :not-acceptable
;;          (constantly (error-page {:status 406, :title "406 - Not acceptable"}))}))))
