(ns kacurez.data-plant.csv.transducers-test
  (:require [clojure.test :refer :all]
            [kacurez.data-plant.csv.transducers :as sut]))

(deftest csv-enclose-columns
  (testing "enclose with doble quote"
    (let [input [["addd" "1" "b" "cf" "" "\n"]]
          result [["addd" "1" "b" "cf" "" "\"\n\""]]]
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

(deftest maps-values-to-colls
  (testing "maps to collections"
    (let [input [{:header [:a :b] :row {:a 1 :b 2}}
                 {:header [:a :b] :row {:a 21 :b 22 :c 33}}]
          result [["1" "2"] ["21" "22"]]]
      (is (= (eduction (sut/maps-values-to-colls) input) result)))))

(deftest supply-header
  (testing "supply header fn"
    (let [input [{:a 1 :b 2} {:a 12 :b 22}]
          result [{:header [:a :b] :row {:a :a :b :b}}
                  {:header [:a :b] :row {:a 1 :b 2}}
                  {:header [:a :b] :row {:a 12 :b 22}}]]
      (is (= (eduction (sut/supply-header) input) result)))))

(deftest maps-collection-to-csv-lines
  (testing "collection maps to csv string"
    (let [input [{"a," "1" "b" "2"} {"a," "21" "b" "22" "c" "33"}]
          result '("\"a,\",b\n"
                   "1,2\n"
                   "21,22\n")]
      (is (= (eduction (sut/maps-to-csv-lines "," "\"") input) result)))))

(deftest str-colls-to-csv-maps
  (testing "str-colls-to-csv-maps fn"
    (let [input [["col1" "col2" "col3"]
                 ["val1" "val2" ""]
                 [1 2 3]]
          result [{"col1" "val1" "col2" "val2" "col3" ""}
                  {"col1" 1 "col2" 2 "col3" 3}]]
      (is (= (eduction (sut/str-colls-to-csv-maps) input) result)))))
