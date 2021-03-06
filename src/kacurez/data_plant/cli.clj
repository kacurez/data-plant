(ns kacurez.data-plant.cli
  (:gen-class)
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [kacurez.data-plant.csv.cli-command :as csv-command]))

(def cli-options
  [["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Generate a file limited to size or number of lines."
        ""
        "Usage: data-plant command [options]"
        ""
        "Commands:"
        "csv - generate a csv file limited to a size or number of lines"
        ""
        "Options:"
        options-summary
        ""]
       (string/join \newline)))

(defn try-parse-subcommand [args]
  (try
    (condp = (first args)
      "csv" (csv-command/parse-args (rest args))
      nil)
    (catch Exception e {:exit-message (str (first args) " command parse error: "
                                           (.getMessage e))})))

(defn validate-args
  "Validate command line arguments. Either return a map indicating the program
  should exit (with a error message, and optional ok status), or a map
  indicating the limit the program should take and the options provided."
  [args]
  (let [parsed-options (parse-opts args cli-options :in-order true)
        {:keys [options arguments errors summary]} parsed-options
        subcommand (try-parse-subcommand arguments)]
    (cond
      (:help options) ; help => exit OK with usage summary
      {:exit-message (usage summary) :ok? true}

      errors ; errors => exit with description of errors
      {:exit-message (string/join \newline errors)}

      ;; parse subcommand
      (and (some? subcommand) (empty? (:exit-message subcommand)))
      {:subcommand  subcommand}

      :else ; failed custom validation => exit with usage summary
      {:exit-message (or (:exit-message subcommand) (usage summary))})))

(defn exit [status msg]
  (binding [*out* *err*]
    (println msg))
  (System/exit status))

(defn -main [& args]
  (let [{:keys [subcommand exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      ((:run subcommand)))))
