MEM=256m
MINMEM=-Xms1m
run:
	time java -XX:+UseG1GC $(MINMEM) -Xmx$(MEM) -jar target/data-plant-0.1.0-SNAPSHOT-standalone.jar -m kacurez.data-plant.transducers

compile: uberjar native-image

all: clean compile run

uberjar:
	lein uberjar

native-image:
	lein native-image

clean:
	rm -rf target
