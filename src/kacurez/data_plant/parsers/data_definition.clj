(ns kacurez.data-plant.parsers.data-definition
  (:require [clojure.tools.reader.edn :as edn]
            [kacurez.data-plant.generators :as gen]))

(def symbols-specs-map
  {'int gen/random-number
   'string gen/random-string
   'date gen/random-date
   'uuid gen/random-uuid
   'float gen/random-float
   'boolean gen/random-boolean
   'pos-int gen/random-pos-int
   'neg-int gen/random-neg-int})

(defn make-symbol-gen-fn [symbol-def]
  (if-let [symbol-fn (symbols-specs-map symbol-def)]
    symbol-fn
    (constantly symbol-def)))

(defn- constant? [value]
  (some #(% value) [int? string? boolean? float? char?]))

(defn- prepare-string [definition-string]
  (let [trimed-string (clojure.string/trim definition-string)]
    (if (and (not= \{ (first trimed-string)) (not= \} (last trimed-string)))
      (str "{" trimed-string "}")
      trimed-string)))

(defn- parse-definition-value [_])

(defn- make-oneof-gen-fn [oneof-options-list]
  (let [options (map parse-definition-value oneof-options-list)]
    (if (empty? options)
      (constantly "")
      (fn []
        (let [option-gen-fn (nth options (gen/random-number (count options)))]
          (option-gen-fn))))))

(defn- parse-definition-value [def-value]
  (cond
    (constant? def-value) (constantly def-value)
    (symbol? def-value) (make-symbol-gen-fn def-value)
    (list? def-value) (condp = (first def-value)
                        'oneOf (make-oneof-gen-fn (rest def-value)))
    (nil? def-value) (constantly "")
    :else (throw (Exception. (str "Unknwon data type to parse: " (type def-value) " value:" (str def-value))))))

(defn- parse-definition-pair [[def-name, def-value]]
  [def-name (parse-definition-value def-value)])

(defn parse-definition-map [definition-string]
  (try
    (let [definition-map (edn/read-string (prepare-string definition-string))]
      (into {} (map parse-definition-pair definition-map)))
    (catch Exception e (throw (Exception. (str "data definition parse error: "
                                               (.getMessage e)))))))

(defn parse-to-xform [definition-string]
  (let [functions-map (parse-definition-map definition-string)]
    (apply comp (map (fn [[key gen-fn]] (map #(assoc % (str key) (str (gen-fn))))) functions-map))))
