(ns uap-clj-lambda.core-spec
  (:refer-clojure :exclude [read])
  (:require [speclj.core :refer [context describe it should==]]
            [clojure.java.io :as io :refer [reader]]
            [cheshire.core :as json :refer [parse-stream]]
            [uap-clj-lambda.core :refer [handle-event]]))

(def ua "Lenovo-A288t_TD/S100 Linux/2.6.35 Android/2.3.5 Release/02.29.2012 Browser/AppleWebkit533.1 Mobile Safari/533.1 FlyFlow/1.4")

(def full-req (json/parse-stream (io/reader "dev-resources/event_lookup_all.json") true))
(def browser-req (json/parse-stream (io/reader "dev-resources/event_lookup_browser.json") true))
(def device-req (json/parse-stream (io/reader "dev-resources/event_lookup_device.json") true))
(def os-req (json/parse-stream (io/reader "dev-resources/event_lookup_os.json") true))
(def multi-req (json/parse-stream (io/reader "dev-resources/event_lookup_multiple.json") true))

(def full-result
  {:results
    [{:ua ua
      :browser {:family "Baidu Explorer", :major "1", :minor "4", :patch ""}
      :os {:family "Android", :major "2", :minor "3", :patch "5", :patch_minor ""}
      :device {:family "Lenovo A288t_TD", :brand "Lenovo", :model "A288t_TD"}}]})

(def browser-result
  {:results
    [{:ua ua
      :browser {:family "Baidu Explorer", :major "1", :minor "4", :patch ""}}]})

(def device-result
  {:results
    [{:ua ua
      :device {:family "Lenovo A288t_TD", :brand "Lenovo", :model "A288t_TD"}}]})

(def os-result
  {:results
    [{:ua ua
      :os {:family "Android", :major "2", :minor "3", :patch "5", :patch_minor ""}}]})

(def multi-result
  {:results
   [{:ua
     "Lenovo-A288t_TD/S100 Linux/2.6.35 Android/2.3.5 Release/02.29.2012 Browser/AppleWebkit533.1 Mobile Safari/533.1 FlyFlow/1.4",
     :browser
     {:family "Baidu Explorer", :major "1", :minor "4", :patch ""},
     :os
     {:family "Android",
      :major "2",
      :minor "3",
      :patch "5",
      :patch_minor ""},
     :device
     {:family "Lenovo A288t_TD", :brand "Lenovo", :model "A288t_TD"}}
    {:browser {:family "Firefox", :major "3", :minor "0", :patch "19"},
     :ua
     "Mozilla/5.0 (X11; U; Linux i686; en-US; rv:1.9.0.19) Gecko/2010031218 FreeBSD/i386 Firefox/3.0.19,gzip(gfe),gzip(gfe)"}
    {:device {:family "HTC Amaze 4G", :brand "HTC", :model "Amaze 4G"},
     :ua
     "Mozilla/5.0 (Linux; U; Android 4.0.3; en-us; Amaze_4G Build/IML74K) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30"}
    {:os
     {:family "Android",
      :major "2",
      :minor "3",
      :patch "6",
      :patch_minor ""},
     :ua
     "UCWEB/2.0 (Linux; U; Adr 2.3.6; en-US; HUAWEI_Y210-0251) U2/1.0.0 UCBrowser/8.6.0.318 U2/1.0.0 Mobile"}]})

(context "event handling"
  (describe "single full lookup"
    (it "takes a useragent string and returns browser, device, and o/s fields"
      (should== full-result
                (handle-event full-req))))
  (describe "single browser lookup"
    (it "takes a useragent string and returns browser fields"
      (should== browser-result
                (handle-event browser-req))))
  (describe "single device lookup"
    (it "takes a useragent string and returns device fields"
      (should== device-result
                (handle-event device-req))))
  (describe "single o/s lookup"
    (it "takes a useragent string and returns o/s fields"
      (should== os-result
                (handle-event os-req))))
  (describe "multiple mixed lookup"
    (it "handles multiple mixed queries in one payload"
      (should== multi-result
                (handle-event multi-req)))))
