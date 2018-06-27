(ns clj-vchats.routes.services.audio
  (:require [org.httpkit.client :as http]
            [clojure.xml :as xml]))

(def api-key "xxxxxxxxxxxxxxxxxxxxxxxxxxxxx")

(defn body-message [text voice]
  (with-out-str
    (xml/emit
     {:tag :speak :attrs {:version "1.1"}
      :content [
                {:tag :voice :attrs {:name voice}}
                text]})))

(defn get-audio [#^String room #^String text
                 & {:keys [voice] :or {voice "sumire"}}]
  (let [dt (clj-time.coerce/to-long (clj-time.local/local-now))
        {:keys [status headers body error] :as resp}
        @(http/post
          (str "https://api.apigw.smt.docomo.ne.jp/aiTalk/v1/textToSpeech?APIKEY="
               api-key)
          :body (body-message text message))
        ;; (with-open [w (clojure.java.io/writer (str "./" room "/" dt ".mp3")) :append false])
        ]))

