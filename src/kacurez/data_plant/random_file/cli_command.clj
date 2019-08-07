(ns kacurez.data-plant.random-file.cli-command
  (:require [kacurez.data-plant.generators :refer [random-string-var-size]]
            [kacurez.data-plant.writer :refer [transduce-to-stream]]))

;;; todo unfinished
(defn generate [gzip?]
  (transduce-to-stream *out* #(str (random-string-var-size 500) "\n") gzip?))
