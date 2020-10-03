(ns kacurez.data-plant.csv.core
  (:require [clojure.data.csv :refer [read-csv]]
            [kacurez.data-plant.csv.transducers :refer [maps-to-csv-lines str-colls-to-csv-maps]]
            [kacurez.data-plant.output
             :refer
             [transduce-coll->stream transduce-file->stream]]))

(defn generate-random-csv-to-stream
  [parsed-size output-stream definition-xf
   {:keys [delimiter enclosure gzip?]
    :or {delimiter "," enclosure "\"" gzip? false}}]
  (let [size-limit-xf (:xform parsed-size)
        csv-xf (maps-to-csv-lines delimiter enclosure)
        xf (comp definition-xf csv-xf size-limit-xf)]
    (transduce-coll->stream output-stream xf (repeat {}) gzip?)))

(defn transduce-csv-file->stream
  [csv-file-path output-stream definition-xf {:keys [delimiter enclosure gzip?]
                                              :or {delimiter "," enclosure "\"" gzip? false}}]
  (let
   [delimiter-char (-> delimiter char-array first)
    enclosure-char (-> enclosure char-array first)
    csv-xf (maps-to-csv-lines delimiter enclosure)
    xf (comp (str-colls-to-csv-maps) definition-xf csv-xf)
    split-to-csv-lines-reader-fn (fn [input-reader] (read-csv input-reader :separator delimiter-char :quote enclosure-char))]
    (transduce-file->stream csv-file-path output-stream xf split-to-csv-lines-reader-fn gzip?)))
