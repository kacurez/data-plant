(ns kacurez.data-plant.generators
  (:require [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as gen]))

(def ascii-chars (map char (range 32 127)))

(defn random-string
  ([fix-size] (random-string fix-size ascii-chars))
  ([fix-size chars] (apply str (take fix-size (repeatedly #(rand-nth chars))))))

(defn random-string-var-size
  ([max-size] (random-string-var-size max-size ascii-chars))
  ([max-size chars]
   (apply str (take (rand-int max-size) (repeatedly #(rand-nth chars))))))

(defn random-string-seq
  ([seq-size string-size] (random-string-seq seq-size string-size ascii-chars))
  ([seq-size string-size chars]
   (map (fn [_] (random-string string-size chars)) (range seq-size))))

(defn random-string-map
  ([map-keys string-size] (random-string-map map-keys string-size ascii-chars))
  ([map-keys string-size chars]
   (into {} (map #(vec (list % (random-string string-size chars))) map-keys))))

(defn random-map-from-spec [spec-map]
  (into {} (map (fn [[k v]] (vec (list k (gen/generate (s/gen v))))) spec-map)))
