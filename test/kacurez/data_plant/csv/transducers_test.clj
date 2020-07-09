(ns kacurez.data-plant.csv.transducers-test
  (:require [clojure.test :refer :all]
            [kacurez.data-plant.csv.transducers :as sut]))

(deftest csv-enclose-columns
  (testing "enclose with doble quote"
    (let [input [["addd" "b" "cf" "" "\n"]]
          result [["addd" "b" "cf" "" "\"\n\""]]]
      (is (= (eduction (sut/csv-enclose-columns "," "\"") input) result))))
  (testing "enclose with |"
    (let [input [["addd" "b" "cf" "," "\n"]]
          result [["addd" "b" "cf" "|,|" "|\n|"]]]
      (is (= (eduction (sut/csv-enclose-columns "," "|") input) result))))
  (testing "enclose and escape with |"
    (let [input [["ad,|dd" "bbbbb|aa|aa" "c\"f" "" "\n" "||"]]
          result [["|ad,||dd|" "|bbbbb||aa||aa|" "c\"f" "" "|\n|" "||||||"]]]
      (is (= (eduction (sut/csv-enclose-columns "," "|") input) result))))
  (testing "enclose and escape with double quote"
    (let [input [["add\"d" "b,\r" "cf" "" "\n\"" "\"\""]]
          result [["\"add\"\"d\"" "\"b,\r\"" "cf" "" "\"\n\"\"\"" "\"\"\"\"\"\""]]]
      (is (= (eduction (sut/csv-enclose-columns "," "\"") input) result)))))

(deftest csv-delimit-columns
  (testing "delimit columns with ,"
    (let [input [["a" "b" "c"] ["d" "e" "f"]]
          result ["a,b,c" "d,e,f"]]
      (is (= (eduction (sut/csv-delimit-columns ",") input) result)))))

(deftest add-new-line
  (testing "add new line"
    (let [input ["a,b,c" "d,e,f"]
          result ["a,b,c\n" "d,e,f\n"]]
      (is (= (eduction (sut/add-new-line) input) result)))))

(deftest colls-to-csv-stringlines
  (testing "collections to csv string lines"
    (let [input [["a" "b" "c"] ["d" "e" "f"]]
          result ["a,b,c\n" "d,e,f\n"]]
      (is (= (eduction (sut/colls-to-csv-stringlines "," "|") input) result)))))

(deftest add-header-coll
  (testing "add header to the collection"
    (let [input [["a1" "b1"] ["a2" "b2"]]
          result [["columnA" "columnB"] ["a1" "b1"] ["a2" "b2"]]]
      (is (= (eduction (sut/add-header-coll ["columnA" "columnB"]) input) result)))))

(deftest maps-values-to-colls
  (testing "maps to collections"
    (let [input [{:header [:a :b] :row {:a 1 :b 2}}
                 {:header [:a :b] :row {:a 21 :b 22 :c 33}}]
          result [[1 2] [21 22]]]
      (is (= (eduction (sut/maps-values-to-colls) input) result)))))

(deftest maps-collection-to-csv-lines
  (testing "collection maps to csv string"
    (let [input [{"a," "1" "b" "2"} {"a," "21" "b" "22" "c" "33"}]
          result '("\"a,\",b\n"
                   "1,2\n"
                   "21,22\n")]
      (is (= (eduction (sut/maps-to-csv-lines ["a," "b"] "," "\"") input) result)))))
