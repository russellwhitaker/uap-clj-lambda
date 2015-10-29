(defproject uap-clj-lambda "0.1.0"
  :description "Amazon AWS Lambda function wrapper around uap-clj"
  :url "https://github.com/russellwhitaker/uap-clj-lambda"
  :license {:name "The MIT License (MIT)"
            :url "http://www.opensource.org/licenses/mit-license.php"}
  :scm {:name "git"
        :url "https://github.com/russellwhitaker/uap-clj-lambda"}
  :dependencies [[org.clojure/clojure "1.7.0"]
                 [uap-clj "1.0.3"]
                 [org.clojure/data.json "0.2.6"]
                 [uswitch/lambada "0.1.0"]]
  :profiles {:dev {:resource-paths ["dev-resources"]
                   :dependencies [[speclj "3.3.1"]]
                   :test-paths ["spec"]}
             :uberjar {:aot :all
                       :uberjar-name "simple-useragent-lambda.jar"
                       :uberjar-exclusions
                         [#"tests|test_resources|docs|\.md|LICENSE|META-INF"]}}
  :plugins [[speclj "3.3.1"]])
