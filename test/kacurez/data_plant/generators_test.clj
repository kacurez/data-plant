(ns kacurez.data-plant.generators-test
  (:require [kacurez.data-plant.generators :as sut]
            [clojure.test :refer :all]))


(deftest generateros-numbers-test
  (testing "abs"
    (dorun (map #(is (>= (sut/abs %) 0)) [1 2 -5 -2132323 0 100 1010])))
  (testing "random-numbers-and-others"
    (dorun 5 (repeatedly #(is (number? (sut/random-number)))))
    (dorun (map #(is (>= % (sut/random-number %))) [1 2 3 150]))
    (dorun 5 (repeatedly #(is (>= (sut/random-pos-int) 0))))
    (dorun 5 (repeatedly #(is (<= (sut/random-neg-int) 0))))
    (dorun 5 (repeatedly #(is (= java.util.Date (type (sut/random-date))))))
    (dorun 5 (repeatedly #(is (= java.util.UUID (type (sut/random-uuid))))))
    (dorun 5 (repeatedly #(is (float? (sut/random-float)))))
    (dorun 5 (repeatedly #(is (boolean? (sut/random-boolean))))))
  (testing "random-string"
    (dorun 5 (repeatedly #(is (string? (sut/random-string)))))
    (dorun (map #(is (= %  (count (sut/random-string %)))) [10 20 1000 5 500 999]))
    (dorun (map #(is (>= %  (count (sut/random-string-var-size %)))) [10 20 1000 5 500 999]))
    (dorun (map
            #(is
              (every? (partial clojure.string/includes? "abcd1")
                      (map str (sut/random-string % "abcd1"))))  [10 20 1000 5 500 999]))
    (dorun (map
            #(is
              (every? (partial clojure.string/includes? "abcd1")
                      (map str (sut/random-string-var-size % "abcd1"))))  [10 20 1000 5 500 999]))))
