(ns kacurez.data-plant.map-generator-builder
  (:require [clojure.tools.reader.edn :as edn]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(def symbols-specs-map
  {'int int?
   'uuid uuid?
   'string string?
   'float float?
   'boolean boolean?
   'pos-int pos-int?
   'neg-int neg-int?
   'nat-int nat-int?})

(defn make-symbol-gen-fn [symbol-def]
  (if-let [symbol-spec (symbols-specs-map symbol-def)]
    (fn [] (gen/generate (s/gen symbol-spec)))
    (constantly symbol-def)))

(defn constant? [value]
  (some #(% value) [int? string? boolean? float? char?]))

(defn- prepare-string [definition-string]
  (let [trimed-string (clojure.string/trim definition-string)]
    (if (and (not= \{ (first trimed-string)) (not= \} (last trimed-string)))
      (str "{" trimed-string "}")
      trimed-string)))

(defn parse-definition-value [_])

(defn- make-oneof-gen-fn [oneof-options-list]
  (if-let [options (map parse-definition-value oneof-options-list)]
    (fn []
      (let [option-gen-fn (nth options (rand-int (count options)))]
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
