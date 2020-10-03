(ns kacurez.data-plant.parsers.xform-definition)

(defn parse-to-xform [definition-string]
  (try
    (eval (read-string (str "(comp " definition-string ")")))
    (catch Exception e (throw (Exception. (str "xform definition parse error: "
                                               (.getMessage e)))))))
