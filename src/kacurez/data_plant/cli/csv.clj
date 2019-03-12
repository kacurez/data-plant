(ns kacurez.data-plant.cli.csv
  (:require   [clojure.string :as string]
              [clojure.tools.cli :refer [parse-opts]]
              [kacurez.data-plant.writers :refer [write-csv-from-maps]]
              [kacurez.data-plant.generators :refer [random-map-from-functions-map]]
              [kacurez.data-plant.size-parser :as size-parser]
              [kacurez.data-plant.map-generator-builder :refer [parse-functions-map]]))

(defn usage [options-summary]
  (->> ["Generate a file limited to size or number of lines."
        ""
        "Usage: data-plant csv limit definition-map"
        ""
        "limits(<number><scale(K|M|G)><unit>(b|bytes|rows)>):"
        "  50MB    - generate 50 megabytes file"
        "  50Krows - generate 50 000 lines file"
        "  1KB  - generate 1 kilobyte file"
        "  2GB  - generate 2 gigabytes file"
        ""
        "definition-map: key value pair where key is column name and value is value"
        ""]
       (string/join \newline)))

(def cli-options
  [["-h" "--help"]])

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn validate-args [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) ; help => exit OK with usage summary
      {:exit-message (usage summary) :ok? true}

      errors ; errors => exit with description of errors
      {:exit-message (error-msg errors)}

      ;; custom validation on arguments
      (and (= 3 (count arguments))
           (= (first arguments) "csv")
           (some? (size-parser/parse (second arguments)))
           (some? (parse-functions-map (nth arguments 2))))
      {:args
       {:limits (size-parser/parse (second arguments))
        :definition-map (parse-functions-map (nth arguments 2))}}

      :else ; failed custom validation => exit with usage summary
      {:exit-message (usage summary)})))

(defn run [{:keys [limits definition-map]}]
  (write-csv-from-maps
   *out*
   #(random-map-from-functions-map definition-map)
   (map str (keys definition-map))
   limits
   {:delimiter "," :enclosure "\""}))
