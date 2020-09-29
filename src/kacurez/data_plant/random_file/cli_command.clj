(ns kacurez.data-plant.random-file.cli-command
  (:require [kacurez.data-plant.commons :refer [transduce-coll->stream]]
            [kacurez.data-plant.generators :refer [random-string-var-size]]))

;;; todo unfinished
(defn generate [gzip?]
  (transduce-coll->stream *out* #(str (random-string-var-size 500) "\n") gzip?))
