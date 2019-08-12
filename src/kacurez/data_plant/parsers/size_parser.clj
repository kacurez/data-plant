(ns kacurez.data-plant.parsers.size-parser
  (:require [kacurez.data-plant.transduction :refer [take-bytes]]))

(def KB 1000)
(def MB (* 1000 KB))
(def GB (* 1000 MB))

(def order-map {"k" KB "m" MB "g" GB})
(def unit-map {"rows" :rows "b" :bytes "bytes" :bytes})

(def size-patern #"(?i)^(\d+)([kmg])?(rows|b|bytes)")

(defn create-parsed-structure [size unit]
  (case unit
    :rows  {:xform (take size)       :value size :unit unit}
    :bytes {:xform (take-bytes size) :value size :unit unit}))

(defn parse [size-str]
  (let [[_ number order unit] (re-matches (re-pattern size-patern) size-str)
        order-num (order-map (clojure.string/lower-case (or order ""))  1)]
    (cond
      (or (nil? number) (nil? unit)) (throw (Exception. (str "invalid size:" size-str)))
      :else
      (let [size (* (Integer/parseInt number) order-num)
            unit (unit-map (clojure.string/lower-case unit))]
        (create-parsed-structure size unit)))))
