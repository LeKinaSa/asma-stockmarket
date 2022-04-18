all: agent

gui:
	java -cp lib/jade.jar jade.Boot -gui

container:
	java -cp lib/jade.jar jade.Boot -container

agent:
	java -cp lib/jade.jar jade.Boot -agents hellow:HelloWorldAgent -gui
