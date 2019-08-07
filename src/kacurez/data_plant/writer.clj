(ns kacurez.data-plant.writer)

(defn- prepare-stream-writer [output-stream gzip?]
  (clojure.java.io/writer
   (if gzip?
     (java.util.zip.GZIPOutputStream. output-stream)
     output-stream)))

(defn transduce-to-stream [output-stream xf coll gzip?]
  (with-open [writer (prepare-stream-writer output-stream gzip?)]
    (transduce (comp
                xf
                (map #(.write writer %)))
               (constantly nil)
               coll)))

(defn transduce-to-file  [filepath xf coll-generator gzip?]
  (with-open [w (clojure.java.io/output-stream filepath)]
    (transduce-to-stream w xf coll-generator gzip?)))
