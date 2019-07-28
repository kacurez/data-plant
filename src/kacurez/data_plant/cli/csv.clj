(ns kacurez.data-plant.cli.csv
  (:require   [clojure.string :as string]
              [clojure.tools.cli :refer [parse-opts]]
              [kacurez.data-plant.writers :refer [write-csv-from-maps]]
              [kacurez.data-plant.generators :refer [random-map-from-functions-map]]
              [kacurez.data-plant.size-parser :as size-parser]
              [kacurez.data-plant.map-generator-builder :refer [parse-functions-map]]))

(def usage
  (->> ["Usage: data-plant csv size definition-map"
        "Generate a csv file to std out limited by size and defined by definition-map."
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

(defn prepare-run-command [size-cli-arg definition-cli-arg]
  (let [parsed-size (size-parser/parse size-cli-arg)
        parsed-definition (parse-functions-map definition-cli-arg)]
    {:run #(run parsed-size parsed-definition)}))

(defn parse-args [args]
  (let [{:keys [options arguments errors]} (parse-opts args cli-options)
        size (first arguments)
        definition (second arguments)]
    (cond
      (:help options)
      {:exit-message usage}

      errors ; errors => exit with description of errors
      {:exit-message (string/join \newline errors)}

      (and
       (some? size)
       (some? definition))
      (prepare-run-command size definition)

      :else {:exit-message usage})))
