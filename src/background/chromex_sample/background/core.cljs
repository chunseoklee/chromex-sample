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
            [chromex.ext.notifications :as noti]))

(def clients (atom []))

(defn mention-count [coll] 
  (reduce + 0 
          (map #(if (= (:reason %) "mention") 1 0) coll)))

(defn rr-count [coll] 
  (reduce + 0 
          (map #(if (= (:reason %) "review_requested") 1 0) coll)))


(defn run-notification-loop []
  (go-loop [noto-id 1]
    (<! (timeout 10000))
    ;;(windows/create (js-obj "url" "https://github.sec.samsung.net/notifications"))
    (go (let [response (<! (http/get "https://github.sec.samsung.net/api/v3/notifications"
                                     {:with-credentials? true
                                      :headers {"authorization"  "token 60cd823e11d4736431c9d0037470779df34e57f5"}}))]
          (prn (:status response))
          (let [mention (mention-count (:body response))
                rr (rr-count (:body response))
                notis (count (:body response))]
            (noti/create (str noti-id) (js-obj
                                        "type" "basic"
                                        "iconUrl" "../images/notification.png"
                                        "message" (str mention " mention(s)\n"
                                                       rr " review request(s)\n"
                                                       notis " notifications")
                                        "title" "Review Time")))))
    (recur (inc noti-id))))

(defn init! []
  (log "BACKGROUND: init")
  (run-notification-loop))
