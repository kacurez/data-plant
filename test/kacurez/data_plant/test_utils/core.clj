(ns kacurez.data-plant.test-utils.core)

(defn
  ^{:doc "Deletes the given directory even if it contains files or subdirectories. This function will attempt to delete
all of the files and directories in the given directory first, before deleting the directory. If the directory cannot be
deleted, this function aborts and returns nil. If the delete finishes successfully, then this function returns true."}
  recursive-delete [directory]
  (if (.isDirectory directory)
    (when (reduce #(and %1 (recursive-delete %2)) true (.listFiles directory))
      (.delete directory))
    (.delete directory)))

(defn mk-path [& args]
  (clojure.string/join "/" args))

(defn mk-tmp-dir!
  "Creates a unique temporary directory on the filesystem. Typically in /tmp on
  *NIX systems. Returns a File object pointing to the new directory. Raises an
  exception if the directory couldn't be created after 100 tries."
  [prefix]
  (let [base-dir (java.io.File. (System/getProperty "java.io.tmpdir"))
        base-name (str prefix (java.util.Date.) "-" (long (rand 100)) "-")
        tmp-base (mk-path base-dir base-name)
        max-attempts 100]
    (loop [num-attempts 1]
      (if (= num-attempts max-attempts)
        (throw (Exception. (str "Failed to create temporary directory after " max-attempts " attempts.")))
        (let [tmp-dir-name (str tmp-base num-attempts)
              tmp-dir (java.io.File. tmp-dir-name)]
          (if (.mkdir tmp-dir)
            tmp-dir
            (recur (inc num-attempts))))))))
