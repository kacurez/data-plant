(defproject kacurez/data-plant "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.2"]
                 [org.clojure/data.generators "0.1.2"]
                 [org.clojure/tools.reader "1.3.3"]
                 [org.clojure/tools.cli "0.4.1"]
                 [org.clojure/data.csv "1.0.0"]]
  :main ^:skip-aot kacurez.data-plant.cli
  :plugins [[io.taylorwood/lein-native-image "0.3.1"]]
  :target-path "target/"
  :uberjar-name "data-plant.jar"
  :native-image {:opts ["--verbose"
                        "--no-fallback"
                        "--no-server"
                        "--initialize-at-build-time"
                        "--enable-url-protocols=http,https"
                        "-H:ReflectionConfigurationFiles=reflection.json"
                        "-H:+ReportExceptionStackTraces"
                        "--report-unsupported-elements-at-runtime"]
                 :name "data-plant"}

  ;; optionally set profile-specific :native-image overrides
  :profiles {:test    ;; e.g. lein with-profile +test native-image
             {:native-image {:opts ["--report-unsupported-elements-at-runtime"
                                    "--verbose"]}}

             :docker-build
             {:native-image {:opts ["--verbose"
                                    "--no-fallback"
                                    "--no-server"
                                    "--static"
                                    "--initialize-at-build-time"
                                    "--enable-url-protocols=http,https"
                                    "-H:ReflectionConfigurationFiles=reflection.json"
                                    "-H:+ReportExceptionStackTraces"
                                    "--report-unsupported-elements-at-runtime"]}}

             :uberjar ;; used by default
             {:aot :all
              :native-image {:opts ["-Dclojure.compiler.direct-linking=true"]}}}

  ;; :jvm-opts ["-Xmx256m"]
  )
