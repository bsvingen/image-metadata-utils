(ns com.borkdal.clojure.image-metadata-utils-test
  (:require [midje.sweet :refer :all]
            [midje.util :refer [testable-privates]]
            [com.borkdal.clojure.image-metadata-utils :refer :all]))

(testable-privates com.borkdal.clojure.image-metadata-utils
                   transform-iptc-entries)

(def ^:private ^:const test-iptc-entries
  '(["Date Created" "20150102"]
    ["Time Created" "123550"]
    ["Digital Creation Date" "20150102"]
    ["Digital Creation Time" "123550"]
    ["Keywords" "Atlantic Road"]
    ["Keywords" "clouds"]
    ["Keywords" "Moere"]
    ["Keywords" "Norway"]
    ["Keywords" "ocean"]
    ["Keywords" "sky"]
    ["Keywords" "sun"]
    ["Keywords" "sunset"]
    ["Keywords" "sunset/sunrise"]))

(def keywords-transducer
  (comp (map-keyword clojure.string/lower-case)
        (remove-keyword "sunset")
        (remove-keyword "sky")
        (replace-keyword "clouds" "lots of clouds")))

(fact "transform-iptc-entries"
  (transform-iptc-entries keywords-transducer test-iptc-entries)
  => [["Date Created" "20150102"]
      ["Time Created" "123550"]
      ["Digital Creation Date" "20150102"]
      ["Digital Creation Time" "123550"]
      ["Keywords" "atlantic road"]
      ["Keywords" "lots of clouds"]
      ["Keywords" "moere"]
      ["Keywords" "norway"]
      ["Keywords" "ocean"]
      ["Keywords" "sun"]
      ["Keywords" "sunset/sunrise"]])

