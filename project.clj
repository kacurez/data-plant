(defproject kacurez/data-plant "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.9.0"]
                 [org.clojure/test.check "0.9.0"]
                 [org.clojure/tools.cli "0.4.1"]]
  :main ^:skip-aot kacurez.data-plant.cli.core
  :plugins [[io.taylorwood/lein-native-image "0.3.0"]]
  :target-path "target/%s"
  :native-image {:graal-bin :env/GRAALVM_HOME
                 :opts ["--verbose" "--no-server" "--static" "--enable-url-protocols=http,https" "-H:ReflectionConfigurationFiles=reflection.json" "--report-unsupported-elements-at-runtime" ]
                 :name "data-plant"}

  ;; optionally set profile-specific :native-image overrides
  :profiles {
             :test    ;; e.g. lein with-profile +test native-image
             {:native-image {:opts ["--report-unsupported-elements-at-runtime"
                                    "--verbose"]}}

             :uberjar ;; used by default
             {:aot :all
              :native-image {:opts ["-Dclojure.compiler.direct-linking=true"]}}}

  ;; :jvm-opts ["-Xmx256m"]
  )
