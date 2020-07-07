(ns kacurez.data-plant.csv.cli-command
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [kacurez.data-plant.csv.core :refer [write-csv-to-stream]]
            [kacurez.data-plant.parsers.data-definition
             :refer
             [parse-definition-map]]
            [kacurez.data-plant.parsers.size-parser :as size-parser]))

(defn usage [options-summary]
  (->> ["Usage: data-plant csv size definition-map"
        "Generate a csv file to std out limited by size and defined by definition-map."
        ""
        "size (<number><scale(K|M|G)><unit>(b|bytes|rows)>):"
        "  50MB    - generate 50 megabytes file"
        "  50Krows - generate 50 000 lines file"
        "  1KB  - generate 1 kilobyte file"
        "  2GB  - generate 2 gigabytes file"
        ""
        "definition-map: sequence of key value pairs where key is column name and value is value"
        ""
        "Options:"
        options-summary
        ""]
       (string/join \newline)))

(def cli-options
  [["-h" "--help"]
   ["-g" "--gzip-output" "gzip output, size is not applied to the gziped content(default false)" :id :gzip? :default false]
   ["-e" "--enclosure ENCLOSURE" "csv enclosure" :default "\""]
   ["-d" "--delimiter DELIMITER" "csv delimiter" :default ","]])

(defn prepare-run-command [size-cli-arg definition-cli-arg options]
  (let [parsed-size (size-parser/parse-to-xform size-cli-arg)
        parsed-definition (parse-definition-map definition-cli-arg)]
    {:run #(write-csv-to-stream System/out parsed-size parsed-definition options)}))

(defn parse-args [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)
        size (first arguments)
        definition (second arguments)]
    (cond
      (:help options)
      {:exit-message (usage summary)}

      errors ; errors => exit with description of errors
      {:exit-message (string/join \newline errors)}

      (and
       (some? size)
       (some? definition))
      (prepare-run-command size definition options)

      :else {:exit-message (usage summary)})))
