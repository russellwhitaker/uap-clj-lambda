(ns uap-clj-lambda.core
  (:refer-clojure :exclude [read])
  (:require [uswitch.lambada.core :refer [deflambdafn]]
            [clojure.data.json :as json :refer [read write]]
            [clojure.java.io :as io :refer [reader writer]]
            [uap-clj.core :refer [lookup-useragent]]))

(defn handle-event
  [event]
  (lookup-useragent (get-in event ["useragent"])))

(deflambdafn uap-clj-lambda.core.SimpleUseragentLookup
  [in out _]
  (let [event (json/read (io/reader in))
        result (handle-event event)]
    (with-open [wtr (io/writer out)]
      (json/write result wtr))))
