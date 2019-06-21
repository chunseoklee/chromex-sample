(ns chromex-sample.background.core
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [goog.string :as gstring]
            [goog.string.format]
            [cljs.core.async :refer [<! chan timeout]]
            [chromex.logging :refer-macros [log info warn error group group-end]]
            [chromex.chrome-event-channel :refer [make-chrome-event-channel]]
            [chromex.protocols.chrome-port :refer [post-message! get-sender]]
            [chromex.ext.tabs :as tabs]
            [chromex.ext.runtime :as runtime]
            [chromex.ext.windows :as windows]
            [chromex-sample.background.storage :refer [test-storage!]]))

(def clients (atom []))


(defn run-notification-loop []
  (go-loop []
    (<! (timeout 10000))
    (windows/create (js-obj "url" "https://github.sec.samsung.net/notifications"))
    (recur)))

(defn init! []
  (log "BACKGROUND: init")
  (run-notification-loop))
