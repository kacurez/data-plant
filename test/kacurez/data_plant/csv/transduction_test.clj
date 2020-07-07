(ns kacurez.data-plant.csv.transduction-test
  (:require [clojure.test :refer :all]
            [kacurez.data-plant.csv.transducers :as sut]))

(deftest csv-enclose-columns
  (testing "enclose with DQ"
    (let [input [["addd" "b" "cf" "" "\n"]]
          result [["\"addd\"" "\"b\"" "\"cf\"" "\"\"" "\"\n\""]]]
      (is (= (eduction (sut/csv-enclose-columns "\"") input) result))))
  (testing "enclose with |"
    (let [input [["addd" "b" "cf" "" "\n"]]
          result [["|addd|" "|b|" "|cf|" "||" "|\n|"]]]
      (is (= (eduction (sut/csv-enclose-columns "|") input) result))))
  (testing "enclose and escape with |"
    (let [input [["ad|dd" "bbbbb|aa|aa" "c\"f" "" "\n" "||"]]
          result [["|ad||dd|" "|bbbbb||aa||aa|" "|c\"f|" "||" "|\n|" "||||||"]]]
      (is (= (eduction (sut/csv-enclose-columns "|") input) result))))
  (testing "enclose and escape with DQ"
    (let [input [["add\"d" "b" "cf" "" "\n\"" "\"\""]]
          result [["\"add\"\"d\"" "\"b\"" "\"cf\"" "\"\"" "\"\n\"\"\"" "\"\"\"\"\"\""]]]
      (is (= (eduction (sut/csv-enclose-columns "\"") input) result)))))

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
          result ["|a|,|b|,|c|\n" "|d|,|e|,|f|\n"]]
      (is (= (eduction (sut/colls-to-csv-stringlines "," "|") input) result)))))
