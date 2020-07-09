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

(defn maps-values-to-colls []
  (map (fn [csv-item] (map #((:row csv-item) % "") (:header csv-item)))))

(defn fetch-header-from-row [row-map former-header]
  (let [new-columns (filter #(not-any? (partial = %) former-header) (keys row-map))
        old-columns (filter (partial contains? row-map) former-header)]
    (concat  old-columns new-columns)))

(defn fetch-header [former-header]
  (fn [xf]
    (let [header (atom nil)]
      (fn
        ([] (xf))
        ([result] (xf result))
        ([result input] (if (some? @header)
                          (xf result {:header @header :row input})
                          ; else
                          (do
                            (reset! header (fetch-header-from-row input former-header))
                            (xf
                             (xf result {:header @header :row (zipmap @header @header)})
                             {:header @header :row input}))))))))

(defn maps-to-csv-lines [header-coll delimiter enclosure]
  (comp
   (fetch-header header-coll)
   (maps-values-to-colls)
   (colls-to-csv-stringlines delimiter enclosure)))
