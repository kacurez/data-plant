(ns kacurez.data-plant.generators
  (:require [clojure.data.generators :as gen]))

(def ascii-chars (map char (range 32 127)))

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

(defn random-pos-int [] (abs (random-number)))

(defn random-neg-int [] (* -1 (random-pos-int)))

(defn random-date []
  (binding [gen/*rnd* (java.util.Random.)]
    (gen/date)))

(defn random-uuid [] (java.util.UUID/randomUUID))

(defn random-float [] (* (gen/float) (random-number)))

(defn random-boolean [] (gen/boolean))

(defn random-string
  ([] (binding [gen/*rnd* (java.util.Random.)]
        (gen/string)))
  ([fix-size] (random-string fix-size ascii-chars))
  ([fix-size chars] (apply str (take fix-size (repeatedly #(rand-nth chars))))))

(defn random-string-var-size
  ([max-size] (random-string-var-size max-size ascii-chars))
  ([max-size chars]
   (apply str (take (rand-int max-size) (repeatedly #(rand-nth chars))))))

#_(defn random-string-seq
  ([seq-size string-size] (random-string-seq seq-size string-size ascii-chars))
  ([seq-size string-size chars]
   (map (fn [_] (random-string string-size chars)) (range seq-size))))

#_(defn random-string-map
  ([map-keys string-size] (random-string-map map-keys string-size ascii-chars))
  ([map-keys string-size chars]
   (into {} (map #(vector % (random-string string-size chars)) map-keys))))

#_(defn random-map-from-spec [spec-map]
    (into {} (map (fn [[k v]] (vector k (gen/generate (s/gen v)))) spec-map)))

#_(defn random-map-from-functions-map [functions-map]
    (into {} (map
              (fn [[key data-generator-function]] (vector (str key) (str (data-generator-function))))
              functions-map)))
