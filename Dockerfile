FROM ubuntu AS BASE
RUN apt-get update
RUN apt-get install -yy curl leiningen build-essential zlib1g-dev
RUN cd /opt && curl -sL https://github.com/oracle/graal/releases/download/vm-1.0.0-rc9/graalvm-ce-1.0.0-rc9-linux-amd64.tar.gz | tar -xzvf -
ADD project.clj .
RUN lein deps
ADD src src
ADD reflection.json reflection.json
ENV GRAALVM_HOME /opt/graalvm-ce-1.0.0-rc9/bin
RUN lein native-image
FROM scratch
COPY --from=BASE /target/default+uberjar/data-plant /
ENTRYPOINT ["/data-plant"]
