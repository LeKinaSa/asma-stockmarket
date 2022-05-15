all: clean compile start

compile:
	./gradlew build
	mkdir bin
	cp -r app/build/classes/java/main/* bin

clean:
	rm -r app/build bin

start:
	java -cp "app/lib/gson.jar:app/lib/jade.jar:bin" jade.Boot -agents \
	"environment:stockmarket.agents.EnvironmentAgent(4); \
	a1:stockmarket.agents.NormalAgent(0.5, 100); \
	a2:stockmarket.agents.NormalAgent(1.0, 100); \
	a3:stockmarket.agents.NormalAgent(1.5, 100); \
	a4:stockmarket.agents.NormalAgent(2.0, 100)" \
	-gui

test1: clean compile
	java -cp "app/lib/gson.jar:app/lib/jade.jar:bin" jade.Boot -agents \
	"environment:stockmarket.agents.EnvironmentAgent(2, 100, 1, 3, 10, 0); \
	a1:stockmarket.agents.NormalAgent(0.5, 100); \
	a2:stockmarket.agents.NormalAgent(0.5, 100)"

test2: clean compile
	java -cp "app/lib/gson.jar:app/lib/jade.jar:bin" jade.Boot -agents \
	"environment:stockmarket.agents.EnvironmentAgent(4, 50, 1, 2, 20); \
	a1:stockmarket.agents.NormalAgent(0.5, 100); \
	a2:stockmarket.agents.NormalAgent(0.6, 100); \
	a3:stockmarket.agents.NormalAgent(0.7, 100); \
	a4:stockmarket.agents.NormalAgent(0.8, 100)"

test3: clean compile
	java -cp "app/lib/gson.jar:app/lib/jade.jar:bin" jade.Boot -agents \
	"environment:stockmarket.agents.EnvironmentAgent(5, 40, 2, 4, 40, 0); \
	a1:stockmarket.agents.NormalAgent(0.1, 10000); \
	a2:stockmarket.agents.NormalAgent(0.2, 10000); \
	a3:stockmarket.agents.NormalAgent(0.3, 10000); \
	a4:stockmarket.agents.NormalAgent(0.4, 10000); \
	a5:stockmarket.agents.NormalAgent(0.5, 10000)"

test4: clean compile
	java -cp "app/lib/gson.jar:app/lib/jade.jar:bin" jade.Boot -agents \
	"environment:stockmarket.agents.EnvironmentAgent(4, 250, 1, 2, 10, 0); \
	a1:stockmarket.agents.NormalAgent(0.5, 100); \
	a2:stockmarket.agents.NormalAgent(1.0, 100); \
	a3:stockmarket.agents.NormalAgent(1.5, 100); \
	a4:stockmarket.agents.NormalAgent(2.0, 100)"

test5: clean compile
	java -cp "app/lib/gson.jar:app/lib/jade.jar:bin" jade.Boot -agents \
	"environment:stockmarket.agents.EnvironmentAgent(2, 10, 2, 1, 50); \
	a1:stockmarket.agents.NormalAgent(); \
	a2:stockmarket.agents.NormalAgent()"
