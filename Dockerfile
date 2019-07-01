FROM ubuntu AS BASE
RUN apt-get update
RUN apt-get install -yy curl leiningen build-essential zlib1g-dev
RUN mkdir /opt/graalvm
RUN cd /opt && curl -sL https://github.com/oracle/graal/releases/download/vm-19.0.2/graalvm-ce-linux-amd64-19.0.2.tar.gz | tar -xzf -
RUN ls /opt/graalvm-ce-19.0.2
RUN /opt/graalvm-ce-19.0.2/bin/gu install native-image
ADD project.clj .
RUN lein deps
ADD src src
ADD reflection.json reflection.json
ENV GRAALVM_HOME /opt/graalvm-ce-19.0.2/bin
RUN lein with-profile +docker-build native-image
FROM scratch
COPY --from=BASE /target/data-plant /
ENTRYPOINT ["/data-plant"]
