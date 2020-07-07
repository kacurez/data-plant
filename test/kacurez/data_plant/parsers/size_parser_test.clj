(ns kacurez.data-plant.parsers.size-parser-test
  (:require [kacurez.data-plant.parsers.size-parser :as sut]
            [clojure.test :refer :all]))

(defn assert-parsed-size [parsed-size expected-value expected-unit]
  (and (= (:unit parsed-size) expected-unit)
       (= (:value parsed-size) expected-value)
       (fn? (:xform parsed-size))))

(defn eval-xform-rows-count [size-str buffer-size]
  (let [parsed-xform (:xform (sut/parse-to-xform size-str))]
    (into '() (eduction parsed-xform (range buffer-size)))))

(defn eval-xform-bytes-count [size-str buffer-size]
  (let [parsed-xform (:xform (sut/parse-to-xform size-str))]
    (mapcat identity (eduction parsed-xform (repeat buffer-size "12345")))))

(deftest size-parse
  (testing "should parse"
    (is (assert-parsed-size (sut/parse-to-xform "1rows") 1 :rows))
    (is (assert-parsed-size (sut/parse-to-xform "2rows") 2 :rows))
    (is (assert-parsed-size (sut/parse-to-xform "2b") 2 :bytes))
    (is (assert-parsed-size (sut/parse-to-xform "2kb") 2000 :bytes))
    (is (assert-parsed-size (sut/parse-to-xform "35krows") 35000 :rows))
    (is (assert-parsed-size (sut/parse-to-xform "2mb")  2000000 :bytes))
    (is (assert-parsed-size (sut/parse-to-xform "145b") 145 :bytes))
    (is (assert-parsed-size (sut/parse-to-xform "140kb") 140000 :bytes))
    (is (assert-parsed-size (sut/parse-to-xform "140gB") 140000000000 :bytes))
    (is (assert-parsed-size (sut/parse-to-xform "1GROWS") 1000000000 :rows))
    (is (assert-parsed-size (sut/parse-to-xform "12345MB") 12345000000 :bytes))
    (is (assert-parsed-size (sut/parse-to-xform "9Mrows") 9000000 :rows)))

  (testing "assert parsed size xform evaluation rows/bytes count"
    (is (= (count (eval-xform-rows-count "2rows" 2)) 2))
    (is (= (count (eval-xform-rows-count "99krows" 200000)) 99000))
    (is (= (count (eval-xform-rows-count "1Mrows" 1200000)) 1000000))
    (is (= (count (eval-xform-bytes-count "2kb" 1000)) 2000))
    (is (= (count (eval-xform-bytes-count "11kb" 10000)) 11000)))

  (testing "assert invalid input"
    (is (thrown-with-msg? Exception #"invalid size:" (sut/parse "")))
    (is (thrown-with-msg? Exception #"invalid size:foobar" (sut/parse "foobar")))
    (is (thrown-with-msg? Exception #"invalid size:2Ktows" (sut/parse "2Ktows")))
    (is (thrown-with-msg? Exception #"invalid size:2r0rows" (sut/parse "2r0rows")))
    (is (thrown-with-msg? Exception #"invalid size:2 rows" (sut/parse "2 rows")))
    (is (thrown-with-msg? Exception #"invalid size:1234" (sut/parse "1234")))
    (is (thrown-with-msg? Exception #"invalid size:rows" (sut/parse "rows")))
    (is (thrown-with-msg? Exception #"invalid size:1brows" (sut/parse "1brows")))
    (is (thrown-with-msg? Exception #"invalid size:2rb" (sut/parse "2rb")))))
