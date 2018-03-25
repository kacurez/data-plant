(ns kacurez.data-plant.helpers)

(defn write-chunk-to-file [wr]
  (let [first? (atom true)]
    (fn
      ([])
      ([result] result)
      ([_ input]
       (if @first? (reset! first? false) (.write wr "\n"))
       (.write wr (clojure.string/join "\n" input))
       nil))))
