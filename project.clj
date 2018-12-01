(defproject kacurez/data-plant "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/core.async "0.4.474"]
                 [org.clojure/test.check "0.9.0"]
                 [org.clojure/tools.cli "0.3.5"]]
  :main kacurez.data-plant.cli.core
  :jvm-opts ["-Xmx256m"])
