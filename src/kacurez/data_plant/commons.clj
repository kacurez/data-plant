(ns kacurez.data-plant.commons
  (:require [clojure.java.io :as io]))

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

(defn- compose-writer [xf writer]
  (comp
   xf
   (map #(.write writer %))))

(defn transduce-coll->stream [output-stream xf coll gzip?]
  (with-open [writer (prepare-stream-writer output-stream gzip?)]
    (transduce
     (compose-writer xf writer)
     (constantly nil)
     coll)))

(defn- prepare-input-stream [path]
  (if (clojure.string/ends-with? path ".gz")
    (java.util.zip.GZIPInputStream. (io/input-stream path))
    ;; else
    (io/input-stream path)))

(defn transduce-file->file [from-path to-path xf input-stream->coll-fn gzip-output?]
  (with-open [reader (io/reader (prepare-input-stream from-path))
              writer (io/output-stream to-path)]
    (transduce-coll->stream writer
                            xf
                            (input-stream->coll-fn reader)
                            gzip-output?)))

(defn transduce-file->stream [from-path to-stream xf input-stream->coll-fn gzip-output?]
  (with-open [reader (io/reader (prepare-input-stream from-path))]
    (transduce-coll->stream to-stream
                            xf
                            (input-stream->coll-fn reader)
                            gzip-output?)))

#_(defn transduce-to-file  [filepath xf coll-generator gzip?]
    (with-open [w (clojure.java.io/output-stream filepath)]
      (transduce-to-stream w xf coll-generator gzip?)))
