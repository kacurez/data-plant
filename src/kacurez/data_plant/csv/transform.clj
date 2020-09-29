(ns kacurez.data-plant.csv.transform
  (:require [clojure.data.csv :refer [read-csv write-csv]]
            [kacurez.data-plant.commons :refer [transduce-to-stream]]
            [kacurez.data-plant.csv.transducers :refer [maps-to-csv-lines]]
            [clojure.java.io :as io]))

(defn csv-data->maps [csv-data csv-header]
  (map zipmap
       (repeat csv-header)
       csv-data))

(defn printxf
  ([] (printxf identity))
  ([fnx]
   (map (fn [x] (println (fnx x)) x))))

(defn set-column [column value]
  (map #(assoc % column value)))

(defn copy-csv [to]
  (with-open [reader (io/reader (java.util.zip.GZIPInputStream. (io/input-stream "test3.gz")))
              writer (io/output-stream to)]
    (let [csv-seq (read-csv reader)
          csv-header (first csv-seq)
          csv-data (rest csv-seq)]
      (transduce-to-stream writer
                           (comp
                            (eval (read-string "(set-column \"aaa\" \"33\")"))
                            #_(printxf #(nil? (% "column4")))
                            ;; (filter #(not-empty (% "column4")))
                            ;; (map #(dissoc % \"column3\"))
                            ;; (map #(assoc % "anewColumn" "2"))
                            (maps-to-csv-lines csv-header "," "\""))
                           (csv-data->maps csv-data csv-header)
                           false))))
