(ns kacurez.data-plant.writers)

(defn- prepare-stream-writer [output-stream gzip?]
  (clojure.java.io/writer
   (if gzip?
     (java.util.zip.GZIPOutputStream. output-stream)
     output-stream)))

(defn write-to-stream
  ([output-stream coll-generator limits] (write-to-stream output-stream (map identity) coll-generator limits))
  ([output-stream xf coll-generator {:keys [gzip?] :or {gzip? false}}]
   (with-open [writer (prepare-stream-writer output-stream gzip?)]
     (transduce (comp
                 xf
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

(defn write-static-content [output-stream string-row limits]
  (write-to-stream output-stream (constantly string-row) limits))
