(ns kacurez.data-plant.csv.writer
  (:require [kacurez.data-plant.csv.transducers
             :refer
             [add-header-coll colls-to-csv-stringlines maps-to-colls]]
            [kacurez.data-plant.writer :refer [transduce-to-stream]]))

(defn transduce-csv-to-stream
  [output-stream maps-generator-fn header-coll size-limiter
   {:keys [delimiter enclosure gzip?]
    :or {delimiter "," enclosure "\"" gzip? false}}]
  (let [map-cols (repeatedly maps-generator-fn)
        csv-xf
        (comp
         (maps-to-colls header-coll)
         (add-header-coll header-coll)
         (colls-to-csv-stringlines delimiter enclosure)
         size-limiter)]
    (transduce-to-stream output-stream csv-xf map-cols gzip?)))
