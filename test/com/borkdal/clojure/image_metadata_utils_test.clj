(ns com.borkdal.clojure.image-metadata-utils-test
  (:require [midje.sweet :refer :all]
            [midje.util :refer [testable-privates]]
            [com.borkdal.clojure.image-metadata-utils :refer :all]))

(testable-privates com.borkdal.clojure.image-metadata-utils
                   transform-iptc-entries)

(def ^:private ^:const test-iptc-entries
  '({:key "Date Created", :value "20150102"}
    {:key "Time Created", :value "123550"}
    {:key "Digital Creation Date", :value "20150102"}
    {:key "Digital Creation Time", :value "123550"}
    {:key "Keywords", :value "Atlantic Road"}
    {:key "Keywords", :value "clouds"}
    {:key "Keywords", :value "Moere"}
    {:key "Keywords", :value "Norway"}
    {:key "Keywords", :value "ocean"}
    {:key "Keywords", :value "sky"}
    {:key "Keywords", :value "sun"}
    {:key "Keywords", :value "sunset"}
    {:key "Keywords", :value "sunset/sunrise"}))

(def keywords-transducer
  (comp (map-keyword clojure.string/lower-case)
        (remove-keyword "sunset")
        (remove-keyword "sky")
        (replace-keyword "clouds" "lots of clouds")))

(fact "transform-iptc-entries"
  (transform-iptc-entries keywords-transducer test-iptc-entries)
  => [{:key "Date Created", :value "20150102"}
      {:key "Time Created", :value "123550"}
      {:key "Digital Creation Date", :value "20150102"}
      {:key "Digital Creation Time", :value "123550"}
      {:key "Keywords", :value "atlantic road"}
      {:key "Keywords", :value "lots of clouds"}
      {:key "Keywords", :value "moere"}
      {:key "Keywords", :value "norway"}
      {:key "Keywords", :value "ocean"}
      {:key "Keywords", :value "sun"}
      {:key "Keywords", :value "sunset/sunrise"}])

