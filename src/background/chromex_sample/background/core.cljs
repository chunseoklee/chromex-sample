(ns chromex-sample.background.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [goog.string :as gstring]
            [goog.string.format]
            [cljs.core.async :refer [<! chan timeout]]
            [cljs-http.client :as http]
            [chromex.logging :refer-macros [log info warn error group group-end]]
            [chromex.chrome-event-channel :refer [make-chrome-event-channel]]
            [chromex.ext.tabs :as tabs]
            [chromex.ext.runtime :as runtime]
            [chromex.ext.windows :as windows]
            [chromex.ext.notifications :as noti]
            [chromex.ext.storage :as storage]
            [chromex.protocols.chrome-storage-area :refer [get set]]))

(def clients (atom []))

(defn mention-count [coll] 
  (reduce + 0 
          (map #(if (= (:reason %) "mention") 1 0) coll)))

(defn rr-count [coll] 
  (reduce + 0 
          (map #(if (= (:reason %) "review_requested") 1 0) coll)))


(defn run-notification-loop [interval]
  (go-loop [noti-id (rand)]
    (<! (timeout (* interval 1000)))
    ;;(windows/create (js-obj "url" "https://github.sec.samsung.net/notifications"))
    (go (let [response (<! (http/get "https://api.github.com/notifications"
                                     {:with-credentials? false
                                      :headers {"authorization"  "token e6eb656562117f50e4eb172382a6b84ff9d526b0"
                                                "Cache-Control" "no-store, no-cache, must-revalidate, post-check=0, pre-check=0"}}))]
          (prn (str noti-id))
          (let [mention (mention-count (:body response))
                rr (rr-count (:body response))
                notis (count (:body response))]
            (noti/create (str noti-id) (js-obj
                                        "type" "basic"
                                        "iconUrl" "../images/notification.png"
                                        "message" (str mention " mention(s)\n"
                                                       rr " review request(s)\n"
                                                       notis " notifications")
                                        "title" "Review Time"
                                        "requireInteraction" false)))))
    (recur (rand))))

(defn init! []
  (log "BACKGROUND: init")
  (go
    (let [[[items] error] (<! (get (storage/get-sync) "fetch_interval"))]
        (if error
          (prn "error")
          (prn (get (js->clj items) "fetch_interval")))))
  (run-notification-loop 10))
   
          
