(ns kacurez.data-plant.parsers.data-definition-test
  (:require [clojure.test :refer :all]
            [kacurez.data-plant.parsers.data-definition :as sut]
            [clojure.set :as set]))

(deftest make-symbol-gen-fn
  (testing "make-symbol-gen-fn test"
    (is (int? ((sut/make-symbol-gen-fn 'int))))
    (is (< ((sut/make-symbol-gen-fn 'neg-int)) 0))
    (is (string? ((sut/make-symbol-gen-fn 'string))))
    (is (= ((sut/make-symbol-gen-fn "abc")) "abc"))
    (is (= ((sut/make-symbol-gen-fn "nul")) "nul"))))

(deftest constant?
  (testing "constant"
    (let [fun-constant #'sut/constant?]
      (is (fun-constant 1))
      (is (fun-constant "a"))
      (is (fun-constant "q"))
      (is (fun-constant true))
      (is (not (fun-constant 'aaa))))))

(deftest prepare-string
  (testing "prepare-string"
    (let [test-fn #'sut/prepare-string]
      (is (= (test-fn "aaa") "{aaa}"))
      (is (= (test-fn "{a  }") "{a  }"))
      (is (= (test-fn "{ }") "{ }"))
      (is (= (test-fn "{ ") "{"))
      (is (= (test-fn "aaa}") "aaa}"))
      (is (= (test-fn "a{a}a") "{a{a}a}")))))

(deftest make-oneof-gen-fn
  (testing "make-oneof-gen-fn"
    (let [test-fn #'sut/make-oneof-gen-fn]
      (is (some? (#{1 2 3} ((test-fn [1 2 3])))))
      (is (some? (#{'a 'b 'c} ((test-fn ['a 'b 'c])))))
      (is (= "" ((test-fn []))))
      (let [value ((test-fn ['int 'b 'c]))]
        (is (or (#{'b 'c} value) (int? value)))))))

(deftest parse-definition-value
  (testing "parse-definition-value"
    (let [test-fn #'sut/parse-definition-value]
      (is (= ((test-fn "a")) "a"))
      (is (= ((test-fn 1)) 1))
      (is (= ((test-fn nil)) ""))
      (is (= ((test-fn 'symbol)) 'symbol))
      (let [value ((test-fn (list 'oneOf 'int 'b 'c)))]
        (is (or (#{'b 'c} value) (int? value))))
      (is (thrown-with-msg? Exception #"Unknwon data type to parse:" (test-fn [1 2]))))))

(deftest parse-definition-map
  (testing "parse-definition-map"
    (is (map? (sut/parse-definition-map "a int")))
    (is (map? (sut/parse-definition-map "a int 1 2")))
    (is (map? (sut/parse-definition-map "a int 1 2 c (oneOf int string 1)")))
    (is (map? (sut/parse-definition-map "{a int b 123 c 'int}")))
    (is (map? (sut/parse-definition-map "{a int b 123 c (oneOf (oneOf 1 2 3))}")))
    (is (thrown-with-msg? Exception #"data definition parse error:" (sut/parse-definition-map "a")))))

(deftest parse-to-xform
  (testing "parse-to-xform"
    (let [values (eduction (sut/parse-to-xform "a 1 b int c (oneOf 1 \"aa\")")
                           [{"oldColumn1" 1} {"oldColumn1" 2 "c" "333"} {"oldColumn1" 1} {"c" "222" "oldColumn1" 1}])]
      (doall (map #(and
                    (is (= (% "a") "1"))
                    (is (some? (#{1 2} (% "oldColumn1"))))
                    (is (int? (Integer/parseInt (% "b"))))
                    (is (some? (#{"1" "aa"} (% "c")))))
                  values)))))
