(ns kacurez.data-plant.cli.core
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [kacurez.data-plant.cli.csv :as csv-command]
            [kacurez.data-plant.size-parser :as size-parser]
            [kacurez.data-plant.cli.random-file :as random-file])
  (:gen-class))

(def cli-options
  [["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Generate a file limited to size or number of lines."
        ""
        "Usage: data-plant command [options]"
        ""
        "Commands:"
        "csv"
        ""
        "Options:"
        options-summary
        ""
        "limits(<number><scale(K|M|G)><unit>(b|bytes|rows)>):"
        "  50MB    - generate 50 megabytes file"
        "  50Krows - generate 50 000 lines file"
        "  1KB  - generate 1 kilobyte file"
        "  2GB  - generate 2 gigabytes file"
        ""]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn try-parse-command [args]
  (csv-command/validate-args args))

(defn validate-args
  "Validate command line arguments. Either return a map indicating the program
  should exit (with a error message, and optional ok status), or a map
  indicating the limit the program should take and the options provided."
  [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options :in-order true)]
    (cond
      (:help options) ; help => exit OK with usage summary
      {:exit-message (usage summary) :ok? true}

      errors ; errors => exit with description of errors
      {:exit-message (error-msg errors)}

      ;; custom validation on arguments
      (and (some? (:args (try-parse-command arguments))))
      (assoc (try-parse-command arguments) :command csv-command/run)

      :else ; failed custom validation => exit with usage summary
      {:exit-message (usage summary)})))

(defn exit [status msg]
  (println msg)
  #_(System/exit status))

(defn -main [& args]
  (let [{:keys [command args options exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (csv-command/run args)
      #_(random-file/generate (size-parser/parse limits)))))
