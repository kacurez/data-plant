MEM=256m
MINMEM=-Xms1m
run:
	java -XX:+UseG1GC $(MINMEM) -Xmx$(MEM) -jar target/data-plant.jar

compile: jar native

all: clean compile

jar:
	lein uberjar

native:
	lein native-image

clean:
	rm -rf target
test-jar: jar
	java -XX:+UseG1GC $(MINMEM) -Xmx$(MEM) -jar target/data-plant.jar csv 10rows "a asdasdasdasd,b int,c date"
