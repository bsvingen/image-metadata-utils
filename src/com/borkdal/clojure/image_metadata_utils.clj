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

