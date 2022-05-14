all: clean compile start

compile:
	./gradlew build
	mkdir bin
	cp -r app/build/classes/java/main/* bin

clean:
	rm -r app/build bin

start:
	java -cp "app/lib/gson.jar:app/lib/jade.jar:bin" jade.Boot -agents \
	"environment:stockmarket.agents.EnvironmentAgent(0); \
	a1:stockmarket.agents.NormalAgent(0.5, 100); \
	a2:stockmarket.agents.NormalAgent(1.0, 100); \
	a3:stockmarket.agents.NormalAgent(1.5, 100); \
	a4:stockmarket.agents.NormalAgent(2.0, 100)" \
	-gui
