(ns kacurez.data-plant.cli-test
  (:require [kacurez.data-plant.cli :as sut]
            [clojure.string :refer [includes?]]
            [clojure.test :refer :all]))

(defn subcommand? [parsed]
  (fn? (-> parsed :subcommand :run)))

(deftest validate-args
  (testing "exit-message"
    (is (:ok? (sut/validate-args ["--help"])))
    (is (:ok? (sut/validate-args ["--help"])))
    (is (includes? (:exit-message (sut/validate-args ["foo" "bar"])) "Usage"))
    (is (includes? (:exit-message (sut/validate-args ["csv"])) "csv"))
    (is (includes? (:exit-message (sut/validate-args ["csv" "aaa"])) "csv"))
    (is (includes? (:exit-message (sut/validate-args ["csv" "-d"])) "Missing required argument"))
    (is (includes? (:exit-message (sut/validate-args ["csv" "-e"])) "Missing required argument")))

  (testing "csv-subcommand"
    (is (subcommand? (sut/validate-args ["csv" "5rows" "a a"])))
    (is (subcommand? (sut/validate-args ["csv" "5rows" "a a" "-g"])))
    (is (subcommand? (sut/validate-args ["csv" "filepath" "a a" "-f"])))
    (is (subcommand? (sut/validate-args ["csv" "5rows" "(map identity)" "-x" "-g"])))
    (is (subcommand? (sut/validate-args ["csv" "5rows" "a a" "-d a"])))))
