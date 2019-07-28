FROM ubuntu AS BASE
ARG GRAAL_VERSION=19.1.0
RUN apt-get update
RUN apt-get install -yy curl leiningen build-essential zlib1g-dev
RUN mkdir /opt/graalvm
RUN cd /opt && curl -sL https://github.com/oracle/graal/releases/download/vm-${GRAAL_VERSION}/graalvm-ce-linux-amd64-${GRAAL_VERSION}.tar.gz | tar -xzf -
RUN ls /opt/graalvm-ce-${GRAAL_VERSION}
RUN /opt/graalvm-ce-${GRAAL_VERSION}/bin/gu install native-image
ADD project.clj .
RUN lein deps
ADD src src
ADD reflection.json reflection.json
ENV GRAALVM_HOME /opt/graalvm-ce-${GRAAL_VERSION}/bin
RUN lein with-profile +docker-build native-image
FROM scratch
COPY --from=BASE /target/data-plant /
ENTRYPOINT ["/data-plant"]
