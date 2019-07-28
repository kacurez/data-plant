(ns kacurez.data-plant.csv.writer
  (:require [kacurez.data-plant.writers :refer [write-to-stream]]
            [kacurez.data-plant.csv.transducers
             :refer [maps-to-colls add-header-coll colls-to-csv-stringlines]]))

(defn write-csv-from-maps
  [output-stream maps-generator header-coll size-limiter
   {:keys [delimiter enclosure]
    :or {delimiter "," enclosure "\""}}]
  (let [csv-from-map-colls
        (comp
         (maps-to-colls header-coll)
         (add-header-coll header-coll)
         (colls-to-csv-stringlines delimiter enclosure)
         size-limiter)]
    (write-to-stream output-stream csv-from-map-colls maps-generator)))

#_(defn write-csv-from-spec [output-stream spec-map limits csv-options]
    (let [header (keys spec-map)
          maps-generator #(random-map-from-spec spec-map)]
      (write-csv-from-maps output-stream  maps-generator header limits csv-options)))

#_(defn write-static-csv [output-stream row-coll limits csv-options]
    (let [row-map (into {} (map #(vec (list % %)) row-coll))]
      (write-csv-from-maps output-stream (constantly row-map) row-coll limits csv-options)))
