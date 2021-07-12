[![travis-ci.org](https://travis-ci.org/bsvingen/image-metadata-utils.svg?branch=master)](https://travis-ci.org/bsvingen/image-metadata-utils)

# clojure.image-metadata-utils

This is a simple library for manipulating image metadata in Clojure.

## Usage

Leiningen coordinate:

```clj
[com.borkdal/clojure.image-metadata-utils "0.1.0-SNAPSHOT"]
```

## Reading

To read generic image metadata, use `get-image-metadata` - the single
argument should be a `java.util.File` object.

Metadata is represented like this:

```clojure
(["Make" "'NIKON CORPORATION'"]
 ["Model" "'NIKON D800E'"]
 ["ExifOffset" "230"]
 ["Keywords" "atlantic road"]
 ["Keywords" "clouds"]
 ["Keywords" "norway"]
 ["Keywords" "sky"]
 ["Keywords" "sunset"])
```

## Manipulating

To manipulate metadata, provide a transducer that transforms entries
on the above form:

```clojure
(def keywords-transducer
	 (comp (map-keyword clojure.string/lower-case)
		   (remove-keyword "sunset")
		   (remove-keyword "sky")
		   (replace-keyword "clouds" "lots of clouds")))

(transform-image-iptc-data input-file output-file keywords-transducer)
```

Only IPTC fields are supported for now, with predefined transducers
for manipulating keywords.

## Limitations

This is very early.

Only tested on JPEGs so far.

