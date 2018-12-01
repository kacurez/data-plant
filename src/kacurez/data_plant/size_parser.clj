(ns kacurez.data-plant.size-parser)

(def KB 1000)
(def MB (* 1000 KB))
(def GB (* 1000 MB))

(def order-map {"k" KB "m" MB "g" GB})
(def unit-map {"rows" :rows "b" :bytes "bytes" :bytes})

(def size-patern #"(?i)^(\d+)([kmg])?(rows|b|bytes)")

(defn parse [size-str]
  (let [[_ number order unit] (re-matches (re-pattern size-patern) size-str)
        order-num (order-map (clojure.string/lower-case (or order ""))  1)]
    #_(println number order-num unit order)
    (if (and number unit)
      {:size (* (Integer/parseInt number) order-num)
       :unit (unit-map (clojure.string/lower-case unit))})))
