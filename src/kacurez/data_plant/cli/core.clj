(ns kacurez.data-plant.cli.core
  (:require [clojure.string :as string]
            [clojure.tools.cli :refer [parse-opts]]
            [kacurez.data-plant.size-parser :as size-parser]
            [kacurez.data-plant.cli.random-file :as random-file])
  (:gen-class)
  )



(def cli-options
  [;; First three strings describe a short-option, long-option with optional
   ;; example argument description, and a description. All three are optional
   ;; and positional.
   ["-z" "--gzip" "gzip result stream"]
   ;; If no required argument description is given, the option is assumed to
   ;; be a boolean option defaulting to nil
   [nil "--detach" "Detach from controlling process"]
   ["-v" nil "Verbosity level; may be specified multiple times to increase value"
    ;; If no long-option is specified, an option :id must be given
    :id :verbosity
    :default 0
    ;; Use assoc-fn to create non-idempotent options
    :assoc-fn (fn [m k _] (update-in m [k] inc))]
   ["-h" "--help"]])

(defn usage [options-summary]
  (->> ["Generate a file limited to size or number of lines."
        ""
        "Usage: data-plant [options] limits"
        ""
        "Options:"
        options-summary
        ""
        "limits(<number><scale(K|M|G)><unit>(b|bytes|rows)>):"
        "  50MB    - generate 50 megabytes file"
        "  50Krows - generate 50 000 lines file"
        "  1KB  - generate 1 kilobyte file"
        "  2GB  - generate 2 gigabytes file"
        ""
        "Please refer to the manual page for more information."]
       (string/join \newline)))

(defn error-msg [errors]
  (str "The following errors occurred while parsing your command:\n\n"
       (string/join \newline errors)))

(defn validate-args
  "Validate command line arguments. Either return a map indicating the program
  should exit (with a error message, and optional ok status), or a map
  indicating the limit the program should take and the options provided."
  [args]
  (let [{:keys [options arguments errors summary]} (parse-opts args cli-options)]
    (cond
      (:help options) ; help => exit OK with usage summary
      {:exit-message (usage summary) :ok? true}
      errors ; errors => exit with description of errors
      {:exit-message (error-msg errors)}
      ;; custom validation on arguments
      (and (= 1 (count arguments))
           (some? (size-parser/parse (first arguments))))
      {:limits (first arguments) :options options}
      :else ; failed custom validation => exit with usage summary
      {:exit-message (usage summary)})))

(defn exit [status msg]
  (println msg)
  (System/exit status))

(defn -main [& args]
  (let [{:keys [limits options exit-message ok?]} (validate-args args)]
    (if exit-message
      (exit (if ok? 0 1) exit-message)
      (random-file/generate (size-parser/parse limits))
      )))
