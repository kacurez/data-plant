(ns kacurez.data-plant.csv.transform
  (:require [clojure.data.csv :refer [read-csv]]
            [clojure.java.io :as io]
            [kacurez.data-plant.csv.transducers :refer [maps-to-csv-lines]]
            [kacurez.data-plant.output
             :refer
             [transduce-coll->stream transduce-file->file]]))

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

(defn fetch-header []
  (fn [xf]
    (let [header (atom nil)]
      (fn
        ([] (xf))
        ([result] (xf result))
        ([result input] (if (some? @header)
                          (xf result (zipmap @header input))
                          ; else
                          (do
                            (reset! header input)
                            (xf result (zipmap @header input)))))))))

(defn copy-csv [to]
  (with-open [reader (io/reader (java.util.zip.GZIPInputStream. (io/input-stream "test3.gz")))
              writer (io/output-stream to)]
    (transduce-coll->stream writer
                            (comp
                             (printxf)
                             (fetch-header)
                             (drop 1)

                             #_(eval (read-string "(set-column \"aaa\" \"33\")"))
                             #_(printxf #(nil? (% "column4")))
                             ;; (filter #(not-empty (% "column4")))
                             ;; (map #(dissoc % \"column3\"))
                             ;; (map #(assoc % "anewColumn" "2"))
                             (maps-to-csv-lines {} "," "\""))
                            (read-csv reader)
                            #_(csv-data->maps csv-data csv-header)
                            false)))
