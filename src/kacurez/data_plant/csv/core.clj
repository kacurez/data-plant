(ns kacurez.data-plant.csv.core
  (:require [kacurez.data-plant.csv.transducers :refer [compose-csv-xf]]
            [kacurez.data-plant.generators :refer [random-map-from-functions-map]]
            [kacurez.data-plant.commons :refer [transduce-to-stream]]))

(defn generate-random-csv-to-stream
  [output-stream parsed-size columns-definition-map
   {:keys [delimiter enclosure gzip?]
    :or {delimiter "," enclosure "\"" gzip? false}}]
  (let [header (map str (keys columns-definition-map))
        random-maps-coll (repeatedly #(random-map-from-functions-map columns-definition-map))
        size-limit-xf (:xform parsed-size)
        csv-xf (compose-csv-xf size-limit-xf header delimiter enclosure)]
    (transduce-to-stream output-stream csv-xf random-maps-coll gzip?)))
