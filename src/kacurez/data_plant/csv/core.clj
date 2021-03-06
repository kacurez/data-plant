(ns kacurez.data-plant.csv.core
  (:require [clojure.data.csv :refer [read-csv]]
            [kacurez.data-plant.csv.transducers :refer [maps-to-csv-lines str-colls-to-csv-maps]]
            [kacurez.data-plant.output
             :refer
             [transduce-coll->stream transduce-file->stream]]))

(defn generate-random-csv->stream
  [parsed-size output-stream transformation-xf
   {:keys [delimiter enclosure gzip?]
    :or {delimiter "," enclosure "\"" gzip? false}}]
  (let [size-limit-xf (:xform parsed-size)
        csv-xf (maps-to-csv-lines delimiter enclosure size-limit-xf)
        xf (comp transformation-xf csv-xf)]
    (transduce-coll->stream output-stream xf (repeat {}) gzip?)))

(defn transform-csv-file->stream
  [csv-file-path output-stream transformation-xf {:keys [delimiter enclosure gzip?]
                                                  :or {delimiter "," enclosure "\"" gzip? false}}]
  (let
   [delimiter-char (-> delimiter char-array first)
    enclosure-char (-> enclosure char-array first)
    csv-xf (maps-to-csv-lines delimiter enclosure)
    xf (comp (str-colls-to-csv-maps) transformation-xf csv-xf)
    split-to-csv-lines-reader-fn (fn [input-reader]
                                   (read-csv input-reader :separator delimiter-char :quote enclosure-char))]
    (transduce-file->stream csv-file-path output-stream xf split-to-csv-lines-reader-fn gzip?)))
