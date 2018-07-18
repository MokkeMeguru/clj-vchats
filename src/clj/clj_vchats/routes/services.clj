(ns clj-vchats.routes.services
  (:require [muuntaja.middleware :as muuntaja]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as rrc]
            [reitit.swagger :as swagger]
            [clj-time.local :as l] ;; added!
            [ring.util.http-response :as response] ;; added!!!
            [ring.middleware.params :as params]
            [clj-vchats.routes.home :as home] ;; added!
            [reitit.coercion.spec :as rcs] ;; added!
            [clj-vchats.routes.ws :as ws] ;; added!
            [clj-vchats.routes.services.auth :as auth]
            [clj-vchats.db.core :as db]
            [clj-vchats.routes.services.closing :refer [create-close-key]]))

(defn service-routes []
  (ring/router
   (conj
    (home/home-routes)
    [
     ;; ["websocket-api"
     ;;  {:swagger {:id ::websocket}}
     ;;  ["/swagger.json"
     ;;   {:get {:no-doc false
     ;;          :swagger {:info {:title "clj-vchats-websocket"}}
     ;;          :handler (swagger/create-swagger-handler)}}]
     ;;  ["/echo" {:get {:handler (fn [req]
     ;;                             (println "chat : get-request=>" req)
     ;;                             (ws/echo-handler req))}}]
     ;;  ["/chat" {:get {:handler (fn [req]
     ;;                             (println "chat : get-request=>" req))}}]]
     ["/test"
      {:swagger {:id ::test}}
      ["/swagger.json"
       {:get {:no-doc false
              :swagger {:info {:title "clj-vchats-api"}}
              :handler (swagger/create-swagger-handler)}}]
      ;; ["/websocket"
      ;;  {:summary "websocket example"
      ;;   :handler ws/ws-handler}]
      ["/websocket/:id"
       {:summary "websocket example"
        :get {:coercion rcs/coercion
              :parameters {:path {:id string?}}
              :handler (fn [req]
                         ;; (println (keys req))
                         ;; (println "channels: "(-> req :parameters :path :id))
                         (ws/ws-handler req))}}]]
     [ "/api"
      {:swagger {:id ::default}}
      ["/swagger.json"
       {:get {:no-doc false
              :swagger {:info {:title "clj-vchats-api"}}
              :handler (swagger/create-swagger-handler)}}]
      ["/tutorial"
       ["/ping" {:summary
                 "Pingを送ります。Pingが返ってきます。"
                 :get ;; changed!
                 (fn [_] {:status 200, :body "ping"})}]
       ["/pong" {:summary
                 "Pongを送ります。Pongが返ってきます。"
                 :post ;; changed!
                 (fn [_] {:status 200, :body "pong"})
                 ;; (constantly "pong")
                 }]
       ["/twice" {:summary
                  "クエリに引数を取ります。二倍されて返ってきます。"
                  :get {:coercion rcs/coercion
                        :parameters {:query {:x int?}}
                        :responses {200 {:body {:result int?}}}
                        :handler
                        (fn [{:keys [parameters]}]
                          {:status 200
                           :body {:result (* 2 (-> parameters :query :x))}})}}]
       ["/plus/:z" {:summary
                    "(やや語弊がありますが)URL上に情報を乗せる方法です。"
                    :description
                    "curl について知識がある場合は \n
curl -X POST --header 'Content-Type: application/json' --header 'Accept: application/json' -d '{\"y\": 2}' 'http://localhost:3000/api/tutorial/plus/3?x=1' \n
を入力してみてください。(curlがわからない場合は無視してください)"
                    :post {:coercion rcs/coercion
                           :parameters {:query {:x int?}
                                        :body {:y int?}
                                        :path {:z int?}}
                           :responses {200 {:body {:total pos-int?}}}
                           :handler (fn [{:keys [parameters]}]
                                      ;; parameters are coerced
                                      (let [x (-> parameters :query :x)
                                            y (-> parameters :body :y)
                                            z (-> parameters :path :z)
                                            total (+ x y z)
                                            ]
                                        {:status 200
                                         :body {:total total}}))}}]]]
     ["/vchats-api"
      {:swagger {:id ::vchats}}
      ["/swagger.json"
       {:get {:no-doc false
              :swagger {:info {:title "clj-vchats-api"}}
              :handler (swagger/create-swagger-handler)}}]
      ["/make-channel" {:summary "チャンネルを作ります。"
                        :description
                        "返ってくる値はチャンネルの名前です。もしチャンネルが作れなければ \"false\" という文字列が返ってきます。(型を統一した弊害です。)\n
Future: チャンネルの名前をランダムな衝突しない値(ex. 00ex4923)に変更します。"
                        :post
                        {:coercion rcs/coercion
                         :parameters {:query {:channel-name string?}}
                         :handler (fn [{:keys [parameters]}]
                                    (let [c-name (-> parameters :query :channel-name)]
                                      {:status 200
                                       :body {:channel-name c-name}}))}}]
      ["/:channel/close-channel" {:summary "チャンネルを閉じます。"
                                   :description
                                   "チャンネルを消すためのワンタイムパスワードを発行します。
エラーならばステータスコード406で error という文字列が返ってきます。チャンネルを閉じる具体的な手順は、ここでチャンネルを消すためのパスワードを入手しそのパスワードを使って {:type close :params key} という json を 該当の WebSocket に流すと、サーバがキーをチェックしてチャンネルを閉じます。チャンネルが閉じられたことは {:closed true} という json が Websocket から流れてくるのを確認して下さい"
                                   :post {:coercion rcs/coercion
                                          :parameters {:path {:channel string?}}
                                          :handler (fn [req]
                                                     (let [parameters (:parameters req)
                                                           uname (:value
                                                                  (get (:cookies req) "identity"))
                                                           chan (-> parameters :path :channel)]
                                                       (println uname ":" chan ":"
                                                                (:master_name
                                                                 (db/get-channel {:chan_name chan})))
                                                       ;;(create-close-key chan)
                                                       (println (create-close-key chan uname))
                                                       (create-close-key chan uname)
                                                       ;; (if (= uname
                                                       ;;        (:master_name
                                                       ;;         (db/get-channel {:chan_name chan})))
                                                       ;;   {:state 200
                                                       ;;    :body (create-close-key chan)}
                                                       ;;   {:status 406
                                                       ;;    :body "error"})
                                                       ))
                                          :middleware [ring.middleware.cookies/wrap-cookies]}}]
      ["/:channel/invite" {:summary "他のユーザを招待します"
                                   :description
                                   "他のユーザを招待します。ここで招待するためのワンタイムパスワードを入手して下さい。それから Websocket に、{:type invite :params {:key key :name name}} という json を送って下さい。サーバでパスワードをチェックして、認証されれば、 name に招待状が届きます。これに name が賛成することでウィンドウがもう一枚生成され、複数画面でのチャットを可能にします"
                                   :post {:coercion rcs/coercion
                                          :parameters {:path {:channel string?}}
                                          :handler (fn [req]
                                                     (let [parameters (:parameters req)
                                                           uname (:value
                                                                  (get (:cookies req) "identity"))
                                                           chan (-> parameters :path :channel)]
                                                       (println uname ":" chan ":" (:master_name
                                                                                    (db/get-channel {:chan_name chan})))
                                                       ;;(create-close-key chan)
                                                       (if (= uname
                                                              (:master_name
                                                               (db/get-channel {:chan_name chan})))
                                                         {:state 200
                                                          :body (create-close-key chan)}
                                                         {:status 406
                                                          :body "error"})))
                                          :middleware [ring.middleware.cookies/wrap-cookies]}}]
      ["/get-channels" {:summary "現在開いているチャンネルを取得します。"
                        :get {:handler
                              (fn [_]
                                {:status 200
                                 :body
                                 {:channels
                                  (map :chan_name (db/get-channels))}})}}]
      ["/:chan/get-messages" {:summary "メッセージを取得します。"
                              :description
                              "自分が閲覧しているチャンネルの過去ログを取得します。\n
取得内容は時系列的に早い順なマップのリストで、マップは {ltime : 送信時間. name : ユーザ名, params : メッセージ} というフォーマットになっています。"
                              :get
                              {:coercion rcs/coercion
                               :parameters {:path {:chan string?}}
                               :handler
                               (fn [{:keys [parameters]}]
                                 (let [c-name (-> parameters :path :chan)]
                                   {:status 200
                                    :body {:messages
                                           (reverse
                                            (map #(assoc % :ltime (clj-time.coerce/to-long (:ltime %)))
                                                 (map #(clojure.set/rename-keys % {:user_name :name
                                                                                   :messages :params})
                                                      (db/get-message {:chan_name c-name}))))
                                           }}))}}]
      ;; ["/login" {:summary "ログインをします。"
;;                  :description "ユーザ登録が済んでいるならばログインすることが出来ます。\n
;; elect / Meguru のみログインできます。(パスワードは任意です)"
;;                  :get {:coercion rcs/coercion
;;                        :parameters {:query {:name string?
;;                                             :pass string?}}
;;                        :handler(fn [header]
;;                                  (let  [a (-> header
;;                                               (merge
;;                                                (ring.util.response/redirect
;;                                                 "/user-page/channels"))
;;                                               (assoc-in [:cookies] {:identity "bar"}
;;                                                         ))]
;;                                    (println "login " a)
;;                                    (println "end")
;;                                    a)
;;                                   )}}]
      ["/:user/logout" {:summary "ログアウトをします。"
                        :post {:coercion rcs/coercion
                               :handler (fn [_]
                                          {:status 200
                                           :body "Exited!"})}}]
      ["/resister!" {:summary "ユーザ登録をします。"
                     :description "名前、パスワード、メールアドレスの3つが必要です。\n
elect / Meguru 以外登録できます。(名前の重複は認めません)\n
Future: メールアドレス認証を行います。"
                     :get {:coercion rcs/coercion
                           :parameters {:query {:name string?
                                                :pass string?
                                                :mail string?}}
                           :responses {200 {:body {:resister? boolean?}}}
                           :handler (fn [{:keys [parameters]}]
                                      {:status 200
                                       :body
                                       {:resister?
                                        (auth/resister! (-> parameters :query))
                                        }})}}]]])
   {:data {:middleware [params/wrap-params
                        muuntaja/wrap-format
                        swagger/swagger-feature
                        rrc/coerce-exceptions-middleware
                        rrc/coerce-request-middleware
                        rrc/coerce-response-middleware]
           :swagger {:produces #{"application/json"
                                 "application/edn"
                                 "application/transit+json"}
                     :consumes #{"application/json"
                                 "application/edn"
                                 "application/transit+json"}}}}))


;; (defn service-routes []
;;   (ring/router
;;     [["/api"
;;       {:swagger {:id ::default}}
;;       ["/swagger.json"
;;        {:get {:no-doc true
;;               :swagger {:info {:title "my-api"}}
;;               :handler (swagger/create-swagger-handler)}}]
;;       ["/ping" {:get (constantly "ping")}]
;;       ["/pong" {:post (constantly "pong")}]]]
;;     {:data {:middleware [params/wrap-params
;;                          muuntaja/wrap-format
;;                          swagger/swagger-feature
;;                          rrc/coerce-exceptions-middleware
;;                          rrc/coerce-request-middleware
;;                          rrc/coerce-response-middleware]
;;             :swagger {:produces #{"application/json"
;;                                   "application/edn"
;;                                   "application/transit+json"}
;;                       :consumes #{"application/json"
;;                                   "application/edn"
;;                                   "application/transit+json"}}}}))
