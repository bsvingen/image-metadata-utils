# clojure.image-metadata-utils

This is a simple library for manipulating image metadata in Clojure.

## Usage

Leiningen coordinate:

```clj
[com.borkdal/clojure.image-metadata-utils "0.1.0-SNAPSHOT"]
```

Requires the 1.7.0 beta of Clojure.

## Reading

To read generic image metadata, use `get-image-metadata` - the single
argument should be a `java.util.File` object.

Metadata is represented like this:

```clojure
({:key "Make", :value "'NIKON CORPORATION'"}
 {:key "Model", :value "'NIKON D800E'"}
 {:key "Orientation", :value "1"}
 {:key "XResolution", :value "240"}
 {:key "YResolution", :value "240"}
 {:key "ResolutionUnit", :value "2"}
 {:key "Software", :value "'Adobe Photoshop Lightroom 6.0 (Macintosh)'"}
 {:key "DateTime", :value "'2015:05:02 19:55:53'"}
 {:key "ExifOffset", :value "230"}
 {:key "Keywords", :value "atlantic road"}
 {:key "Keywords", :value "clouds"}
 {:key "Keywords", :value "moere"}
 {:key "Keywords", :value "norway"}
 {:key "Keywords", :value "ocean"}
 {:key "Keywords", :value "sky"}
 {:key "Keywords", :value "sun"}
 {:key "Keywords", :value "sunset"}
 {:key "Keywords", :value "sunset/sunrise"})
```

## Manipulating

To manipulate metadata, provide a transducer that transforms entries
on the above form:

```clojure
(require '[com.borkdal.clojure.image-metadata-utils :as meta])

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
		   (filter #(match-keyword % "sunset"))))

(meta/transform-image-iptc-data input-file output-file keywords-transducer)
```

Only IPTC fields are supported for now.

## Limitations

This is very early.

Only tested on JPEGs so far.

