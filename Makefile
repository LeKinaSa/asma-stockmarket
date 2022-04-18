all: agent

gui:
	java -cp "lib/jade.jar:bin" jade.Boot -gui

container:
	java -cp "lib/jade.jar:bin" jade.Boot -container

agent:
	java -cp "lib/jade.jar:bin" jade.Boot -agents hellow:HelloWorldAgent -gui

compile:
	javac -cp "lib/jade.jar" src/* -d bin/
