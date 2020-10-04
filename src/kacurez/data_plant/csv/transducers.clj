(ns kacurez.data-plant.csv.transducers)

(defn take-string-coll-bytes
  "return transducers that will count bytes of streamed input collection of strings and force stop when reach the limit-size in bytes"
  [limit-size]
  (fn [xf]
    (let [bytes-count (atom limit-size)]
      (fn
        ([] (xf))
        ([result] (xf result))
        ([result input]
         (let [new-size (- @bytes-count (count (.getBytes (clojure.string/join input))))]
           (if (> @bytes-count 0)
             (do
               (reset! bytes-count new-size)
               (xf result input))
             (reduced result))))))))

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
      ;; else
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

(defn maps-values-to-colls []
  (map (fn [csv-item] (map #(str ((:row csv-item) % "")) (:header csv-item)))))

(defn supply-header []
  (fn [xf]
    (let [header (atom nil)]
      (fn
        ([] (xf))
        ([result] (xf result))
        ([result input] (if (some? @header)
                          (xf result {:header @header :row input})
                          ; else
                          (do
                            (reset! header (keys input))
                            (xf
                             (xf result {:header @header :row (zipmap @header @header)})
                             {:header @header :row input}))))))))

(defn maps-to-csv-lines
  ([delimiter enclosure] (maps-to-csv-lines delimiter enclosure (map identity)))
  ([delimiter enclosure limit-xf]
   (comp
    (supply-header)
    (maps-values-to-colls)
    limit-xf
    (colls-to-csv-stringlines delimiter enclosure))))

(defn str-colls-to-csv-maps []
  (fn [xf]
    (let [header (atom nil)]
      (fn
        ([] (xf))
        ([result] (xf result))
        ([result input] (if (some? @header)
                          (xf result (zipmap @header input))
                          ; else
                          (reset! header input)))))))
