(ns kacurez.data-plant.writers
  (:require [kacurez.data-plant.generators :refer :all]
            [kacurez.data-plant.transducers :refer :all]))

(defn- enforce-limits [limits]
  (cond
    (= (:unit limits) :rows) (take (:size limits))
    (= (:unit limits) :bytes) (take-bytes (:size limits))
    (contains? limits :custom-limit-xform) (:custom-limit-xform limits)
    :else (throw (ex-info "limits must be specified!" {}))))

(defn- prepare-stream-writer [output-stream gzip?]
  (clojure.java.io/writer
   (if gzip?
     (java.util.zip.GZIPOutputStream. output-stream)
     output-stream)))

(defn write-to-stream
  ([output-stream coll-generator limits] (write-to-stream output-stream (map identity) coll-generator limits))
  ([output-stream xf coll-generator limits]
   (with-open [writer (prepare-stream-writer output-stream (:gzip? limits false))]
     (transduce (comp
                 xf
                 (enforce-limits limits)
                 (map #(.write writer %)))
                (constantly nil)
                (if (fn? coll-generator)
                  (repeatedly coll-generator) coll-generator)))))

(defn gen-file
  ([filepath coll-generator limits]
   (gen-file filepath (map identity) coll-generator limits))
  ([filepath xf coll-generator limits]
   (with-open [w (clojure.java.io/output-stream filepath)]
     (write-to-stream w xf coll-generator limits))))

(defn write-csv-from-maps
  [output-stream maps-generator header-coll limits
                    {:keys [delimiter enclosure]
                     :or {delimiter "," enclosure "\""}, :as csv-options}]
  (let [csv-from-map-colls
        (comp
         (maps-to-colls header-coll)
         (add-header-coll header-coll)
         (colls-to-csv-stringlines delimiter enclosure))]
    (write-to-stream output-stream csv-from-map-colls maps-generator  limits )))

(defn write-csv-from-spec [output-stream spec-map limits csv-options]
  (let [header (keys spec-map)
        maps-generator #(random-map-from-spec spec-map)]
    (write-csv-from-maps output-stream  maps-generator header limits csv-options)))

(defn write-static-csv [output-stream row-coll limits csv-options]
  (let [row-map (into {} (map #(vec (list % %)) row-coll))]
    (write-csv-from-maps output-stream (constantly row-map) row-coll limits csv-options)))

(defn write-static-content [output-stream string-row limits]
  (write-to-stream output-stream (constantly string-row) limits))
