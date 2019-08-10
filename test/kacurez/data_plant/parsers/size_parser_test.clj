(ns kacurez.data-plant.parsers.size-parser-test
  (:require [kacurez.data-plant.parsers.size-parser :as sut]
            [clojure.test :refer :all]))

(defn assert-parsed-size [parsed-size expected-value expected-unit]
  (and (= (:unit parsed-size) expected-unit)
       (= (:value parsed-size) expected-value)
       (fn? (:xform parsed-size))))

(defn eval-xform-rows-count [size-str buffer-size]
  (let [parsed-xform (:xform (sut/parse size-str))]
    (into '() (eduction parsed-xform (range buffer-size)))))

(defn eval-xform-bytes-count [size-str buffer-size]
  (let [parsed-xform (:xform (sut/parse size-str))]
    (mapcat identity (eduction parsed-xform (repeat buffer-size "12345")))))

(deftest size-parse
  (testing "should parse"
    (is (assert-parsed-size (sut/parse "1rows") 1 :rows))
    (is (assert-parsed-size (sut/parse "2rows") 2 :rows))
    (is (assert-parsed-size (sut/parse "2b") 2 :bytes))
    (is (assert-parsed-size (sut/parse "2kb") 2000 :bytes))
    (is (assert-parsed-size (sut/parse "35krows") 35000 :rows))
    (is (assert-parsed-size (sut/parse "2mb")  2000000 :bytes))
    (is (assert-parsed-size (sut/parse "145b") 145 :bytes))
    (is (assert-parsed-size (sut/parse "140kb") 140000 :bytes))
    (is (assert-parsed-size (sut/parse "140gB") 140000000000 :bytes))
    (is (assert-parsed-size (sut/parse "1GROWS") 1000000000 :rows))
    (is (assert-parsed-size (sut/parse "12345MB") 12345000000 :bytes))
    (is (assert-parsed-size (sut/parse "9Mrows") 9000000 :rows)))

  (testing "assert parsed size xform evaluation rows/bytes count"
    (is (= (count (eval-xform-rows-count "2rows" 2)) 2))
    (is (= (count (eval-xform-rows-count "99krows" 200000)) 99000))
    (is (= (count (eval-xform-rows-count "1Mrows" 1200000)) 1000000))
    (is (= (count (eval-xform-bytes-count "2kb" 1000)) 2000))
    (is (= (count (eval-xform-bytes-count "1MB" 200000)) 1000000))
    (is (= (count (eval-xform-bytes-count "11kb" 10000)) 11000))))
