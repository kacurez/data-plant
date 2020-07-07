(ns kacurez.data-plant.csv.core
  (:require [kacurez.data-plant.csv.transducers :refer [transduce-csv-to-stream]]
            [kacurez.data-plant.generators :refer [random-map-from-functions-map]]))

(defn write-csv-to-stream [stream parsed-size parsed-definition-map options]
  (let [header (map str (keys parsed-definition-map))
        map-generator-fn #(random-map-from-functions-map parsed-definition-map)
        limit-xf (:xform parsed-size)]
    (transduce-csv-to-stream
     stream
     map-generator-fn
     header
     limit-xf
     options)))
