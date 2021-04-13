(ns kacurez.data-plant.output-test
  (:require [clojure.test :refer :all]
            [kacurez.data-plant.test-utils.core :as test-utils]
            [kacurez.data-plant.output :as sut]))

(def file-content ["line1" "line2" "line3"])
(def big-file-content (map str (take 30000 (iterate inc 1))))

(def ^:dynamic *tmpdir* "")
(defn test-file []
  (str *tmpdir* "/testfile"))

(defn big-test-file []
  (str *tmpdir* "/bigtestfile"))


(defn get-path [filename]
  (str *tmpdir* "/" filename))

(defn setup-tmpdir [f]
  (binding [*tmpdir* (test-utils/mk-tmp-dir! "data-plant-test")]
    (spit (test-file) (clojure.string/join "\n" file-content))
    (spit (big-test-file) (clojure.string/join "\n" big-file-content))
    (f)
    (test-utils/recursive-delete *tmpdir*)))

(def lines-xf (map #(str % "\n")))

(deftest test-transduce-file->file
  (sut/transduce-file->file (test-file) (get-path "testfile.gz") lines-xf line-seq true)
  (sut/transduce-file->file (big-test-file) (get-path "bigtestfile.gz") lines-xf line-seq true)
  (sut/transduce-file->file (get-path "bigtestfile.gz") (get-path "bigresult") lines-xf line-seq false)
  (sut/transduce-file->file (get-path "testfile.gz") (get-path "result") lines-xf line-seq false)
  (sut/transduce-file->file (get-path "testfile.gz") (get-path "result.gz") lines-xf line-seq true)
  (sut/transduce-file->file (get-path "result.gz") (get-path "result2") lines-xf line-seq false)
  (let [actual-content (clojure.string/split-lines (slurp (get-path "result")))
        big-actual-content (clojure.string/split-lines (slurp (get-path "bigresult")))
        actual-content2 (clojure.string/split-lines (slurp (get-path "result2")))]
    (is (= actual-content file-content))
    (is (= big-actual-content big-file-content))
    (is (= actual-content2 file-content))))

(deftest test-transduce-file->stream
  (let [actual-content (clojure.string/split-lines (with-out-str (sut/transduce-file->stream (test-file) *out* (comp (map clojure.string/reverse) lines-xf) line-seq false)))]
    (is (= actual-content (map clojure.string/reverse file-content)))))

(use-fixtures :once setup-tmpdir)
