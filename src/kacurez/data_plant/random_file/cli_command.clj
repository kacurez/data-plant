(ns kacurez.data-plant.random-file.cli-command
  (:require [kacurez.data-plant.generators :refer [random-string-var-size]]
            [kacurez.data-plant.output :refer [transduce-coll->stream]]))

;;; todo unfinished
(defn generate [gzip?]
  (transduce-coll->stream *out* #(str (random-string-var-size 500) "\n") gzip?))
