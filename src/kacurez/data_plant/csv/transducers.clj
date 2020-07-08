(ns kacurez.data-plant.csv.transducers)

(defn escape-pattern-char [string]
  (if (clojure.string/includes? "\\|()[]+.?*^$" string)
    (str "\\" string)
    string))

(defn enclose-string? [string delimiter enclosure]
  (re-find (re-pattern (str "\n|\r|\t|" (escape-pattern-char enclosure) "|" (escape-pattern-char delimiter))) string))

(defn enclose-column [delimiter enclosure]
  (fn [column]
    (if (enclose-string? column delimiter enclosure)
      (str enclosure
           (clojure.string/replace column enclosure (str enclosure enclosure))
           enclosure)
      ; else
      column)))

(defn csv-enclose-columns [delimiter enclosure]
  (map (fn [columns]
         (map (enclose-column delimiter enclosure) columns))))

(defn csv-delimit-columns [delimiter]
  (map #(clojure.string/join delimiter %)))

(defn add-new-line []
  (map #(str % "\n")))

(defn colls-to-csv-stringlines
  ([] (colls-to-csv-stringlines "," "\""))
  ([delimiter enclosure]
   (comp
    (csv-enclose-columns delimiter enclosure)
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
