(ns clj-vchats.routes.services.invite)

(def inviting (atom {})) ;; {chan_name one-time-key}
(def invite (atom {})) ;; {chan_name [name name name ...]}

