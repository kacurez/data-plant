(ns kacurez.data-plant.csv.cli-command
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [kacurez.data-plant.csv.core :refer [generate-random-csv->stream transform-csv-file->stream]]
            [kacurez.data-plant.parsers.data-definition
             :as data-definition]
            [kacurez.data-plant.parsers.xform-definition :as xform-definition]
            [kacurez.data-plant.parsers.size-definition :as size-definition]))

(defn usage [options-summary]
  (->> ["Usage: data-plant csv source definition-map"
        "Generate a csv file to std out limited by source(size/input-file) and defined by definition-map/xf"
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
   ["-x" "--xform-spec" "if true, then data definition is expected to be an xform transducer" :id :xform? :default false]
   ["-f" "--file" "if true source parameter represents input file path to be transformed" :id :file? :default false]
   ["-e" "--enclosure ENCLOSURE" "csv enclosure" :default "\""]
   ["-d" "--delimiter DELIMITER" "csv delimiter" :default ","]])

(defn prepare-run-command [source-cli-arg definition-cli-arg options]
  (let [{:keys [xform? file?]} options
        source (if file? source-cli-arg (size-definition/parse-to-xform source-cli-arg))
        xf (if xform? (xform-definition/parse-to-xform definition-cli-arg)
               (data-definition/parse-to-xform definition-cli-arg))]
    (if file?
      {:run #(transform-csv-file->stream source *out* xf options)}
      ;; else
      {:run #(generate-random-csv->stream source *out* xf options)})))

(defn parse-args [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)
        source (first arguments)
        definition (second arguments)]
    (cond
      (:help options)
      {:exit-message (usage summary)}

      errors ; errors => exit with description of errors
      {:exit-message (string/join \newline errors)}

      (and
       (some? source)
       (some? definition))
      (prepare-run-command source definition options)

      :else {:exit-message (usage summary)})))
