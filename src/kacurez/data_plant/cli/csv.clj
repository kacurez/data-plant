(ns kacurez.data-plant.cli.csv
  (:require   [clojure.string :as string]
              [clojure.tools.cli :refer [parse-opts]]
              [kacurez.data-plant.writers :refer [write-csv-from-maps]]
              [kacurez.data-plant.generators :refer [random-map-from-functions-map]]
              [kacurez.data-plant.size-parser :as size-parser]
              [kacurez.data-plant.map-generator-builder :refer [parse-functions-map]]))

(def usage
  (->> ["Generate a file limited to size or number of lines."
        ""
        "Usage: data-plant csv size definition-map"
        ""
        "size (<number><scale(K|M|G)><unit>(b|bytes|rows)>):"
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

(defn run [parsed-size parsed-definition-map]
  (write-csv-from-maps
   *out*
   #(random-map-from-functions-map parsed-definition-map)
   (map str (keys parsed-definition-map))
   parsed-size
   {:delimiter "," :enclosure "\""}))

(defn parse-args [args]
  (let [{:keys [options arguments errors]} (parse-opts args cli-options)
        parsed-size (size-parser/parse (or (first arguments) ""))
        parsed-definition (parse-functions-map (or (second arguments) ""))]
    (cond
      (:help options)
      {:exit-message usage}

      errors ; errors => exit with description of errors
      {:exit-message (string/join \newline errors)}

      (and
       (some? parsed-size)
       (some? parsed-definition))
      {:run #(run parsed-size parsed-definition)}

      :else {:exit-message usage})))
