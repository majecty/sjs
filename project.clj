(defproject sjs "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.10.1"]
                 [org.clojure/clojurescript "1.10.764"]
                 [ru.prepor/kern "1.2.0-SNAPSHOT"]
                 [failjure "2.1.1"]
                 [com.taoensso/timbre "5.1.0"]]
  :plugins [[lein-cljfmt "0.7.0"]]
  ;;  :main ^:skip-aot sjs.core
  :npm {:dependencies [[source-map-support "0.5.19"]]}
  :src-paths ["src/sjs"]
  :target-path "target/%s")
;;  :eval-in-leiningen true

