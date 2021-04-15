FROM clojure:openjdk-17-lein-alpine
ADD . /code
WORKDIR /code
RUN lein deps
RUN lein uberjar
RUN chmod a+r target/data-plant.jar
ENTRYPOINT ["java", "-jar", "target/data-plant.jar"]
