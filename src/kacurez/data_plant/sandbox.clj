(ns kacurez.data-plant.sandbox
  (:require [clojure.core.async :as async :refer [chan close!]]
            [kacurez.data-plant.transducers :refer :all]))



(defn write-chunk-to-file [wr]
  (let [first? (atom true)]
    (fn
      ([])
      ([result] result)
      ([_ input]
       (if @first? (reset! first? false) (.write wr "\n"))
       (.write wr (clojure.string/join "\n" input))
       nil))))

(defn csv-row-coll->csv-row-str
  ([seq] (csv-row-coll->csv-row-str "," "\"" seq))
  ([delimiter enclosure seq]
   (clojure.string/join delimiter (map #(str enclosure (clojure.string/replace % #"\"" "\"\"") enclosure) seq))))

(defn csv-row-map->csv-row-coll [header input-map]
  (map #(input-map %) header))

(defn map-csv-row->str-fn [header]
  (comp csv-row-coll->csv-row-str
        (partial csv-row-map->csv-row-coll header)))

(defn csvfy [header]
  (fn [xf]
    (let [write-header? (atom true)]
      (fn
        ([] (xf))
        ([result] (xf result))
        ([result input]
         (let [make-row-str (map-csv-row->str-fn header)]
           (if @write-header?
             (do
               (reset! write-header? false)
               (xf (xf result (csv-row-coll->csv-row-str header)) (make-row-str input)))
             (xf result (make-row-str input)))))))))

(defn my-take [initcunt]
  (let [cunt (atom initcunt)]
    (fn [xf]
      (fn
        ([] (xf))
        ([result] (xf result))
        ([result input]
         (if (< @cunt 1)
           (reduced result)
           (do
             (reset! cunt (dec @cunt))
             #_(println "AAAA" input @cunt)
             (xf result input))))))))
(defn tryp []
  (let [c (chan)]
    (async/pipeline
        2
        c
        (comp (my-take 5) (map (partial * 10)) (map inc))
        (async/to-chan (take 10 (iterate inc 0))))
      (loop [val (async/<!! c)]
        (println "value " val)
        (if (not (nil? val))
          (recur (async/<!! c))))))




(defn generate-file
  ([filepath generator-fn limits]
   (generate-file filepath (map identity) generator-fn limits))
  ([filepath xform generator-fn limits]
   (let [xlimit (cond
                  (contains? limits :max-rows-count) (take (:max-rows-count limits))
                  (contains? limits :max-bytes-count) (take-bytes (:max-bytes-count limits))
                  (contains? limits :custom-limit-xform) (:custom-limit-xform limits)
                  :else (throw (ex-info "limits must be specified!" {})))]
     (with-open [w (clojure.java.io/writer filepath)]
       (transduce (comp xform xlimit (partition-all 1000))
                  (write-chunk-to-file w)
                  (repeatedly generator-fn))))))



#_(defn ptest-gen-file [filepath coll]
  (with-open [w (-> filepath
                    clojure.java.io/output-stream
                    #_java.util.zip.GZIPOutputStream.
                    clojure.java.io/writer)]
    (async/pipeline
     2
     (chan 100)
     (comp (xfile) (map #(println %)))
     (async/to-chan (vec (take 10 coll))))))

#_(defn prun-me [filepath]
  (let [data (map #(map (partial str "celle-") %) (partition 2 (iterate inc 0)))]
    (async/<!! (ptest-gen-file filepath data))))

#_(defn run-me [filepath]
  (let [data (map #(map (partial str "celle-") %) (partition 2 (iterate inc 0)))]
    (test-gen-file filepath data)))

#_(defn -main [& args]
  (println "run script" args)
  (run-me (or (first (concat '() args )) "pokusnew")))





(defn write-bytes-to-file [wr]
  (fn
    ([])
    ([result] result)
    ([_ input]
     (.write wr input)
     nil)))
