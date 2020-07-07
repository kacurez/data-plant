(ns kacurez.data-plant.commons)

(defn take-bytes
  "return transducers that will count bytes of streamed input and force stop when reach tje limit-size"
  [limit-size]
  (fn [xf]
    (let [bytes-count (atom limit-size)]
      (fn
        ([] (xf))
        ([result] (xf result))
        ([result input]
         (let [new-size (- @bytes-count (count (.getBytes input)))]
           (if (> @bytes-count 0)
             (do
               (reset! bytes-count new-size)
               (xf result input))
             (reduced result))))))))

(defn- prepare-stream-writer [output-stream gzip?]
  (clojure.java.io/writer
   (if gzip?
     (java.util.zip.GZIPOutputStream. output-stream)
     output-stream)))

(defn compose-writer [xf writer]
  (comp
   xf
   (map #(.write writer %))))

(defn transduce-to-stream [output-stream xf coll gzip?]
  (with-open [writer (prepare-stream-writer output-stream gzip?)]
    (transduce
     (compose-writer xf writer)
     (constantly nil)
     coll)))

(defn transduce-to-file  [filepath xf coll-generator gzip?]
  (with-open [w (clojure.java.io/output-stream filepath)]
    (transduce-to-stream w xf coll-generator gzip?)))
