(ns kacurez.data-plant.csv.core-test
  (:require [kacurez.data-plant.csv.core :as sut]
            [kacurez.data-plant.test-utils.core :as test-utils]
            [clojure.test :refer :all]))

(def file-content ["column" "1" "2"])

(def ^:dynamic *tmpdir* "")
(defn test-file []
  (str *tmpdir* "/testfile"))

(defn setup-tmpdir [f]
  (binding [*tmpdir* (test-utils/mk-tmp-dir! "data-plant-test")]
    (spit (test-file) (clojure.string/join "\n" file-content))
    (f)
    (test-utils/recursive-delete *tmpdir*)))

(defn lines->coll [genfn]
  (clojure.string/split-lines (with-out-str (genfn))))

(def size-xf {:xform (take 2)})

(deftest generate-random-csv->stream
  (is (= 2 (count (lines->coll (fn [] (sut/generate-random-csv->stream size-xf *out* (map #(assoc % :a 1)) {}))))))
  (is (= "a" (first (lines->coll (fn [] (sut/generate-random-csv->stream size-xf *out* (map #(assoc % "a" 1)) {}))))))
  (is (some? (with-out-str  (sut/generate-random-csv->stream size-xf *out* (map identity) {})))))

(deftest transform-csv-file->stream
  (is (= 3 (count (lines->coll (fn [] (sut/transform-csv-file->stream (test-file) *out* (map identity) {})))))))

(use-fixtures :once setup-tmpdir)
