all: clean compile start

compile:
	./gradlew build
	mkdir bin
	cp -r app/build/classes/java/main/* bin

clean:
	rm -r app/build bin

start:
	java -cp "app/lib/gson.jar:app/lib/jade.jar:bin" jade.Boot -agents \
	"environment:stockmarket.agents.EnvironmentAgent; \
	a1:stockmarket.agents.NormalAgent; \
	a2:stockmarket.agents.NormalAgent"
