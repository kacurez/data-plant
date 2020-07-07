MEM=256m
MINMEM=-Xms1m
run:
	time java -XX:+UseG1GC $(MINMEM) -Xmx$(MEM) -jar target/data-plant-0.1.0-SNAPSHOT-standalone.jar

compile: jar native

all: clean compile

jar:
	lein uberjar

native:
	lein native-image

clean:
	rm -rf target
test-jar: jar
	java -XX:+UseG1GC $(MINMEM) -Xmx$(MEM) -jar target/data-plant-0.1.0-SNAPSHOT-standalone.jar csv 10rows "a asdasdasdasd,b int,c date"
