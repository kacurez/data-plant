(ns kacurez.data-plant.transducers)

(defn take-bytes [limit-size]
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

(defn csv-enclose-columns [enclosure]
  (map (fn [columns]
         (map #(str enclosure
                    (clojure.string/replace % enclosure (str enclosure enclosure))
                    enclosure)
              columns))))

(defn csv-delimit-columns [delimiter]
  (map #(clojure.string/join delimiter %)))

(defn add-new-line []
  (map #(str % "\n")))

(defn colls-to-csv-stringlines
  ([] (colls-to-csv-stringlines "," "\""))
  ([delimiter enclosure]
   (comp
    (csv-enclose-columns enclosure)
    (csv-delimit-columns delimiter)
    (add-new-line))))

(defn add-header-coll [header-coll]
  (fn [xf]
    (let [added? (atom false)]
      (fn
        ([] (xf))
        ([result] (xf result))
        ([result input] (if @added? (xf result input)
                            (do
                              (reset! added? true)
                              (xf (xf result header-coll) input))))))))

(defn maps-to-colls [header]
  (map (fn [line-map] (map #(line-map % "") header))))
