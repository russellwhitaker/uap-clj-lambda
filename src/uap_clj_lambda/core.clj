(ns uap-clj-lambda.core
  (:refer-clojure :exclude [read])
  (:require [uswitch.lambada.core :refer [deflambdafn]]
            [cheshire.core :as json :refer [generate-string
                                            parse-stream]]
            [clojure.java.io :as io :refer [reader writer]]
            [taoensso.timbre :as log :refer [info]]
            [uap-clj.core :refer [useragent]]
            [uap-clj.browser :refer [browser]]
            [uap-clj.device :refer [device]]
            [uap-clj.os :refer [os]]))

(defn lookup
  "Look up full useragent fields, or only lookup device,
   o/s, or browser fields.
  "
  [query]
  (let [{:keys [ua lookup]} query
        parser (if (= "useragent" lookup)
                 (symbol "uap-clj.core/useragent")
                 (symbol (str "uap-clj." lookup) lookup))]
    (if (= "useragent" lookup)
      ((resolve parser) ua)
      (merge {(keyword lookup) ((resolve parser) ua)}
             {:ua ua}))))

(defn handle-event
  "Get request and look up against all useragents therein
  "
  [event]
  {:results (into [] (map lookup (:queries event)))})

(deflambdafn uap-clj-lambda.core.UseragentLookup
  [in out _]
  (let [event (json/parse-stream (io/reader in) true)
        result (handle-event event)]
    (log/info result)
    (with-open [wtr (io/writer out)]
      (.write wtr (json/generate-string result)))))
