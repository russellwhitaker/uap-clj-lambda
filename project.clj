(defproject uap-clj-lambda "1.0.0"
  :description "Amazon AWS Lambda function wrapper around uap-clj"
  :url "https://github.com/russellwhitaker/uap-clj-lambda"
  :license {:name "The MIT License (MIT)"
            :url "http://www.opensource.org/licenses/mit-license.php"}
  :scm {:name "git"
        :url "https://github.com/russellwhitaker/uap-clj-lambda"}
  :dependencies [[org.clojure/clojure   "1.8.0"]
                 [uap-clj               "1.3.1"]
                 [cheshire              "5.6.3"]
                 [com.taoensso/timbre   "4.8.0"]
                 [uswitch/lambada       "0.1.2"]]
  :profiles {:dev {:resource-paths ["dev-resources"]
                   :dependencies [[speclj "3.3.2"]]
                   :test-paths ["spec"]}
             :uberjar {:aot :all
                       :uberjar-name "simple-useragent-lambda.jar"
                       :uberjar-exclusions
                         [#"dev_resources|^test$|test_resources|docs|\.md|LICENSE|META-INF"]}}
  :plugins [[speclj       "3.3.2"]
            [lein-ancient "0.6.10"]]
  :aliases {"test"  ["do" ["clean"] ["spec" "--reporter=d"]]
            "build" ["do" ["clean"] ["uberjar"]]})
