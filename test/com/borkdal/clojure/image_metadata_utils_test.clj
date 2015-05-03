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

(defn lower-case-keyword
  [iptc-entry]
  {:key (:key iptc-entry)
   :value (clojure.string/lower-case (:value iptc-entry))})

(defn match-keyword
  [iptc-entry
   keyword]
  (or (not (= (:key iptc-entry) "Keywords"))
      (not (= (:value iptc-entry) keyword))))

(def keywords-transducer
  (comp (map lower-case-keyword)
        (filter #(match-keyword % "sunset"))
        (filter #(match-keyword % "sky"))))

(fact "transform-iptc-entries"
  (transform-iptc-entries keywords-transducer test-iptc-entries)
  => [{:key "Date Created", :value "20150102"}
      {:key "Time Created", :value "123550"}
      {:key "Digital Creation Date", :value "20150102"}
      {:key "Digital Creation Time", :value "123550"}
      {:key "Keywords", :value "atlantic road"}
      {:key "Keywords", :value "clouds"}
      {:key "Keywords", :value "moere"}
      {:key "Keywords", :value "norway"}
      {:key "Keywords", :value "ocean"}
      {:key "Keywords", :value "sun"}
      {:key "Keywords", :value "sunset/sunrise"}])

