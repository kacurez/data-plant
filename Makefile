MEM=256m
MINMEM=-Xms1m
run:
	time java -XX:+UseG1GC $(MINMEM) -Xmx$(MEM) -jar target/data-plant-0.1.0-SNAPSHOT-standalone.jar -m  kacurez.data-plant.transducers
install:
	lein uberjar
all: install run
