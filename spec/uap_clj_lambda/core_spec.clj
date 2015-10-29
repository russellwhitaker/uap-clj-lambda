(ns uap-clj-lambda.core-spec
  (:refer-clojure :exclude [read])
  (:require [speclj.core :refer :all]
            [clojure.data.json :as json :refer [read write]]
            [clojure.java.io :as io :refer [reader writer]]
            [uap-clj-lambda.core :refer [handle-event]]))

(def payload (json/read (io/reader "dev-resources/event_payload.json")))
(def result {:ua "Lenovo-A288t_TD/S100 Linux/2.6.35 Android/2.3.5 Release/02.29.2012 Browser/AppleWebkit533.1 Mobile Safari/533.1 FlyFlow/1.4",
             :browser {:family "Baidu Explorer", :major "1", :minor "4", :patch ""},
             :os {:family "Android", :major "2", :minor "3", :patch "5", :patch_minor ""},
             :device {:family "Lenovo A288t_TD", :brand "Lenovo", :model "A288t_TD"}})

(describe "Event handler output"
  (it "Looks up a useragent string and emits browser, device, and o/s information"
    (should== result (handle-event payload))))
