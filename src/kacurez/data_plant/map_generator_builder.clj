(ns kacurez.data-plant.map-generator-builder
  (:require [clojure.tools.reader.edn :as edn]
            [clojure.data.generators :as gen]))

(def max-random-int Integer/MAX_VALUE)

(defn abs [number]
  (if (> 0 number)
    (* -1 number)
    number))

(defn random-number
  ([max-random-int] (abs (rem (random-number) max-random-int)))
  ([]
   (binding [gen/*rnd* (java.util.Random.)]
     (gen/int))))

(defn random-string []
  (binding [gen/*rnd* (java.util.Random.)]
    (gen/string)))

(defn random-date []
  (binding [gen/*rnd* (java.util.Random.)]
    (gen/date)))

(def symbols-specs-map
  {'int random-number
   'string random-string
   'date random-date
   'uuid (fn [] (java.util.UUID/randomUUID))
   'float #(* (gen/float) (random-number))
   'boolean #(gen/boolean)
   'pos-int #(abs (random-number))
   'neg-int #(* -1 (abs (random-number)))})

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
  (if-let [options (map parse-definition-value oneof-options-list)]
    (fn []
      (let [option-gen-fn (nth options (random-number (count options)))]
        (option-gen-fn)))
    (constantly "")))

(defn- parse-definition-value [def-value]
  (cond
    (constant? def-value) (constantly def-value)
    (symbol? def-value) (make-symbol-gen-fn def-value)
    (list? def-value) (condp = (first def-value)
                        'oneOf (make-oneof-gen-fn (rest def-value)))
    (nil? def-value) (constantly "nil")))

(defn parse-functions-map [definition-string]
  (let [definition-map (edn/read-string (prepare-string definition-string))]
    (into {} (map
              (fn [[def-name, def-value]] [def-name (parse-definition-value def-value)])
              definition-map))))
