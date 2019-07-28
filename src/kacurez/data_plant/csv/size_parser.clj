(ns kacurez.data-plant.csv.size-parser
  (:require [kacurez.data-plant.transducers :refer [take-bytes]]))

(def KB 1000)
(def MB (* 1000 KB))
(def GB (* 1000 MB))

(def order-map {"k" KB "m" MB "g" GB})
(def unit-map {"rows" :rows "b" :bytes "bytes" :bytes})

(def size-patern #"(?i)^(\d+)([kmg])?(rows|b|bytes)")

(defn create-size-limiter [size unit]
  (case unit
    :rows (take size)
    :bytes (take-bytes size)))

#_(defn- enforce-limits [limits]
    (cond
      (= (:unit limits) :rows) (take (:size limits))
      (= (:unit limits) :bytes) (take-bytes (:size limits))
      (contains? limits :custom-limit-xform) (:custom-limit-xform limits)
      :else (throw (ex-info "limits must be specified!" {}))))

(defn parse [size-str]
  (let [[_ number order unit] (re-matches (re-pattern size-patern) size-str)
        order-num (order-map (clojure.string/lower-case (or order ""))  1)]
    (cond
      (nil? number) (throw (Exception. (str "size parse error: wrong number:" size-str)))
      (nil? unit) (throw (Exception. (str "size parse error: wrong unit:" size-str)))
      (and (= order-num 1) (some? order)) (throw (Exception. (str "size parse error: wrong scale:" size-str)))
      :else
      (let [size (* (Integer/parseInt number) order-num)
            unit (unit-map (clojure.string/lower-case unit))]
        (create-size-limiter size unit)))))
