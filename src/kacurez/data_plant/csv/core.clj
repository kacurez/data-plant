(ns kacurez.data-plant.csv.core
  (:require [kacurez.data-plant.csv.transducers :refer [maps-to-csv-lines]]
            [kacurez.data-plant.generators :refer [random-map-from-functions-map]]
            [kacurez.data-plant.commons :refer [transduce-coll->stream transduce-file->stream]]
            [clojure.data.csv :refer [read-csv]]))

(defn generate-random-csv-to-stream
  [output-stream parsed-size columns-definition-map
   {:keys [delimiter enclosure gzip?]
    :or {delimiter "," enclosure "\"" gzip? false}}]
  (let [header (map str (keys columns-definition-map))
        random-maps-coll (repeatedly #(random-map-from-functions-map columns-definition-map))
        size-limit-xf (:xform parsed-size)
        csv-xf (maps-to-csv-lines header delimiter enclosure)
        xf (comp csv-xf size-limit-xf)]
    (transduce-coll->stream output-stream xf random-maps-coll gzip?)))

(defn transduce-csv-file->stream
  [csv-file-path output-stream xf {:keys [delimiter enclosure gzip?]
                                   :or {delimiter "," enclosure "\"" gzip? false}}]
  (let [delimiter-char (-> delimiter char-array first)
        enclosure-char (-> enclosure char-array first)
        read-csv-fn #(read-csv % :separator delimiter-char :quote enclosure-char)]
    (transduce-file->stream csv-file-path output-stream xf read-csv-fn gzip?)))
