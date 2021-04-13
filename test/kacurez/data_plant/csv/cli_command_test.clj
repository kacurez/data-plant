(ns kacurez.data-plant.csv.cli-command-test
  (:require [kacurez.data-plant.csv.cli-command :as sut]
            [clojure.test :refer :all]))

(defn run-cli->lines [& args]
  (clojure.string/split-lines (with-out-str ((:run (sut/parse-args args))))))

(deftest run-cli-command
  (testing "basic"
    (is (= 10 (count (run-cli->lines "10rows" "a 1"))))
    (is (= "a" (first (run-cli->lines "10rows" "a 1"))))
    (is (= "1" (second (run-cli->lines "10rows" "a 1"))))
    (is (= "1" (last (run-cli->lines "10rows" "a 1"))))
    (is (= "a,b" (first (run-cli->lines "10rows" "a 1 b 2")))))
  (testing "delimiters"
    (is (= "a|b" (first (run-cli->lines "10rows" "a 1 b 2" "-d|"))))
    (is (= "-a--a-|b" (first (run-cli->lines "10rows" "a-a 1 b 2" "-d|" "-e-")))))
  (testing "-xform-spec"
    (is (= 10 (count (run-cli->lines "10rows" "(map #(assoc % :a 1))" "-x"))))
    #_(is (= 1 (count (run-cli->lines "10rows" "(map #(= % 1))" "-x"))))
    (is (= ":a,:b" (first (run-cli->lines "10B" "(map #(assoc % :a 1 :b 2))" "-x"))))
    (is (= "1,2" (second (run-cli->lines "10B" "(map #(assoc % :a 1 :b 2))" "-x"))))
    (is (= "1,2" (last (run-cli->lines "10B" "(map #(assoc % :a 1 :b 2))" "-x"))))
    (is (= "a" (first (run-cli->lines "10rows" "(map #(assoc % \"a\" 1))" "-x"))))
    (is (= ":a" (first (run-cli->lines "10rows" "(map #(assoc % :a 1))" "-x"))))))
