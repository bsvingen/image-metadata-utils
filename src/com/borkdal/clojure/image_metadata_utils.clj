(ns com.borkdal.clojure.image-metadata-utils
  (require [clojure.java.io :as io])
  (import [java.util List]
          [java.io File]
          [org.apache.commons.imaging Imaging]
          [org.apache.commons.imaging.formats.jpeg.iptc
           IptcTypes
           IptcRecord
           IptcBlock
           JpegIptcRewriter
           PhotoshopApp13Data]))

(defn get-image-metadata
  "Returns all metadata for an image file."
  [file]
  {:pre [(instance? File file)]}
  (map (fn [item] {:key (.getKeyword item)
                   :value (.getText item)})
       (.getItems
        (Imaging/getMetadata file))))

(defn- get-type-map
  []
  (into {}
        (map (juxt #(.getName %) identity)
             (IptcTypes/values))))

(defn- get-iptc-entries
  [photoshop-app-13-data]
  {:pre [(instance? PhotoshopApp13Data photoshop-app-13-data)]}
  (map (fn [item] {:key (.getIptcTypeName item)
                   :value (.getValue item)})
       (.getRecords photoshop-app-13-data)))

(defn- make-photoshop-app-13-data
  [iptc-entries
   raw-blocks]
  {:pre [(every? #(instance? IptcBlock %) raw-blocks)]
   :post [(instance? PhotoshopApp13Data %)]}
  (PhotoshopApp13Data.
   (let [type-map (get-type-map)]
     (map #(IptcRecord. (type-map (:key %)) (:value %))
          iptc-entries))
   raw-blocks))

(defn- transform-iptc-entries
  [xform
   iptc-entries]
  (transduce xform conj iptc-entries))

(defn transform-image-iptc-data
  "Transforms metadata in image file, writing to new file."
  [input-file
   output-file
   xform]
  {:pre [(instance? File input-file)
         (instance? File output-file)]}
  (let [photoshop-app-13-data (.photoshopApp13Data
                               (.getPhotoshop (Imaging/getMetadata input-file)))]
    (with-open [output-stream (io/output-stream output-file)]
      (.writeIPTC (JpegIptcRewriter.)
                  input-file
                  output-stream
                  (make-photoshop-app-13-data
                   (transform-iptc-entries xform (get-iptc-entries photoshop-app-13-data))
                   (.getRawBlocks photoshop-app-13-data))))))

(defn map-keyword
  "Creates a transducer for mapping keywords with provided function."
  [f]
  (map
   (fn [iptc-entry]
     (if (= (:key iptc-entry) "Keywords")
       {:key "Keywords" :value (f (:value iptc-entry))}
       iptc-entry))))

(defn replace-keyword
  "Creates a transducer for replacing keywords."
  [before
   after]
  (map-keyword
   (fn [keyword]
     (if (= keyword before)
       after
       keyword))))

(defn remove-keyword
  "Creates a transducer for removing keywords."
  [keyword]
  (filter
   (fn [iptc-entry]
     (not
      (and (= (:key iptc-entry) "Keywords")
           (= (:value iptc-entry) keyword))))))

