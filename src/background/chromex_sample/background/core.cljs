(ns chromex-sample.background.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [goog.string :as gstring]
            [goog.string.format]
            [cljs.core.async :refer [<! chan timeout]]
            [chromex.logging :refer-macros [log info warn error group group-end]]
            [chromex.chrome-event-channel :refer [make-chrome-event-channel]]
            [chromex.ext.tabs :as tabs]
            [chromex.ext.runtime :as runtime]
            [chromex.ext.windows :as windows]
            [chromex.ext.notifications :as noti]))

(def clients (atom []))

(defn run-notification-loop []
  (go-loop [noto-id 1]
    (<! (timeout 10000))
    ;;(windows/create (js-obj "url" "https://github.sec.samsung.net/notifications"))
    (noti/create (str noti-id) (js-obj
                     "type" "basic"
                     "iconUrl" "../images/notification.png"
                     "message" "Hey, Get your review done !"
                     "title" "review time"))
    (recur (inc noti-id))))

(defn init! []
  (log "BACKGROUND: init")
  (run-notification-loop))
