(ns clj-vchats.routes.services.audio
  (:require [org.httpkit.client :as http]
            [clojure.xml :as xml]
            [clojure.data.codec.base64 :as b64]
            )
  (:import [javax.net.ssl SSLEngine SNIHostName SSLParameters])
  (:use [clojure.java.shell :only [sh]]))

;; --------------------------------------------------------
(def api-key "3936337a2f48334e7a4c786c42376e746a52466f32424157352e536968474479642f6e574e465730484d33")

(defn sni-configure [#^SSLEngine ssl-engine uri]
  (let [#^SSLParameters ssl-params (.getSSLParameters ssl-engine)]
    (.setServerNames ssl-params [(SNIHostName. (.getHost uri))])
    (.setSSLParameters ssl-engine ssl-params)))

(def client (http/make-client {:ssl-configurer sni-configure}))

;; -------------------------------

(defn body-message [text voice rate pitch]
  (with-out-str
    (xml/emit
     {:tag :speak :attrs {:version "1.1"}
      :content [{:tag :voice :attrs {:name voice}
                 :content [{:tag :prosody :attrs
                            {:rate rate
                             :pitch pitch}
                            :content [text]}]}]})))

(defn get-audio [#^String text
                 & {:keys [voice rate pitch] :or {voice "sumire" rate 1.0 pitch 1.0}}]
  (let [dt (clj-time.coerce/to-long (clj-time.local/local-now))
        {:keys [status headers body error] :as resp}
        @(http/request
         {:url (str "https://api.apigw.smt.docomo.ne.jp/aiTalk/v1/textToSpeech?APIKEY="
                    api-key)
          :method :post
          :headers {"Content-Type" "application/ssml+xml"
                    "Accept" "audio/L16"}
          :keepalive 3000
          :client client
          :body (body-message text voice rate pitch)
          })]
    (if-not error body nil)))

;; (body-message "こんにちわ" "sumire")

(defn raw->wav [bin]
  (when bin
    (let [tfile (java.io.File/createTempFile "./tmp-" ".raw")
          t-path (.getAbsolutePath tfile)
          _ (with-open
              [w (clojure.java.io/output-stream tfile)]
              (.write w bin))
          ofile (java.io.File/createTempFile "./out-" ".wav")
          o-path (.getAbsolutePath ofile)
          _ (sh "sox" "-t" "raw" "-r" "16k" "-e"
                "signed" "-b" "16" "-B" "-c" "1" t-path o-path)
          _ (.delete tfile)
          copy-buf (with-open [in (clojure.java.io/input-stream ofile)]
                     (let [buf (byte-array (.length ofile))
                           _ (.read in buf)]
                       buf))]
      copy-buf)))

;; -----------------------------------------

;; please check exist of the room.

(defn wav->base64 [wav]
  (b64/encode wav))

(defn get-audio-b64 [text & {:keys [voice rate pitch] :or {voice "sumire" rate 1.0 pitch 1.0}}]
  (-> (get-audio text :voice voice :rate rate :pitch pitch)
      .bytes
      raw->wav
      wav->base64
      String.))

;; (def tmp
;;   (let [buf (-> (get-audio
;;                  "こんにちわ"
;;                  :voice
;;                  "sumire"
;;                  :rate 1.0
;;                  :pitch 1.5)
;;                 .bytes
;;                 raw->wav
;;                 wav->base64)]
;;     buf))
