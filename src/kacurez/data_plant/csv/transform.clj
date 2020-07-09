(ns kacurez.data-plant.csv.transform
  (:require [clojure.data.csv :refer [read-csv write-csv]]
            [kacurez.data-plant.commons :refer [transduce-to-stream]]
            [kacurez.data-plant.csv.transducers :refer [maps-to-csv-lines]]
            [clojure.java.io :as io]))

(defn csv-data->maps [csv-data]
  (map zipmap
       (->> (first csv-data) ;; First row is the header
            #_(map keyword) ;; make columns as keywords
            repeat)
       (rest csv-data)))

(defn printxf
  ([] (printxf identity))
  ([fnx]
   (map (fn [x] (println (fnx x)) x))))

(defn copy-csv [to]
  (with-open [reader (io/reader (java.util.zip.GZIPInputStream. (io/input-stream "test3.gz")))
              writer (io/output-stream to)]
    (let [csv-seq (read-csv reader)
          csv-header (first csv-seq)]
      (transduce-to-stream writer
                           (comp
                            #_(printxf #(nil? (% "column4")))
                            ;; (filter #(not-empty (% "column4")))
                            ;; (map #(dissoc % "column3"))
                            ;; (map #(assoc % "anewColumn" "1"))
                            (maps-to-csv-lines csv-header "," "\""))
                           (csv-data->maps csv-seq)
                           false))))
