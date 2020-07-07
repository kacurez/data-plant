(ns kacurez.data-plant.csv.transducers)

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

(defn maps-values-to-colls [header]
  (map (fn [line-map] (map #(line-map % "") header))))

(defn maps-to-csv-lines [header-coll delimiter enclosure]
  (comp
   (maps-values-to-colls header-coll)
   (add-header-coll header-coll)
   (colls-to-csv-stringlines delimiter enclosure)))
