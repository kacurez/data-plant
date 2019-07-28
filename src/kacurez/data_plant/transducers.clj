(ns kacurez.data-plant.transducers)

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
