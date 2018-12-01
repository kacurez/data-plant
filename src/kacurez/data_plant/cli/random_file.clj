(ns kacurez.data-plant.cli.random-file
  (:require [kacurez.data-plant.writers :refer [write-to-stream]]
            [kacurez.data-plant.generators :refer [random-string-var-size]])
  )

(defn generate [limits]
  (write-to-stream *out* #(str (random-string-var-size 500) "\n") limits))
