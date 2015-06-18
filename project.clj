(defproject com.borkdal/clojure.image-metadata-utils "0.1.0-SNAPSHOT"
  :description "Image metadata utilities for Clojure"
  :url "https://github.com/bsvingen/image-metadata-utils"
  :license {:name "Apache License 2.0"
            :url "http://www.apache.org/licenses/LICENSE-2.0"}
  :repositories {"Apache Development Snapshot Repository"
                 "https://repository.apache.org/content/repositories/snapshots/"}
  :dependencies [[org.clojure/clojure "1.7.0-RC2"]
                 [org.apache.commons/commons-imaging "1.0-SNAPSHOT"]]
  :profiles {:dev {:dependencies [[midje "1.7.0-beta1"]]
                   :plugins [[lein-midje "3.1.3"]]}})
