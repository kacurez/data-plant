(ns kacurez.data-plant.parsers.xform-definition-test
  (:require [kacurez.data-plant.parsers.xform-definition :as sut]
            [clojure.test :refer :all]))

(deftest parse-to-xform
  (testing "parse-to-xform"
    (is (fn? (sut/parse-to-xform "(map inc)")))
    (is (thrown-with-msg? Exception #"xform definition parse error:" (sut/parse-to-xform "blblabla")))))
