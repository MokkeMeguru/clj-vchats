(ns clj-vchats.routes.services.closing)

(def closing (atom {})) ;; {chan_name one-time-key}


(defn random-str []
  (apply str (take 50 (repeatedly #(char (+ (rand 26) 65))))))

;; (random-str) this is the one-time-key


(defn create-close-key [chan-name]
  (let [s (random-str)]
    (swap! closing assoc chan-name s)
    s))

