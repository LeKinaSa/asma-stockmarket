all: clean compile agent

compile:
	javac -cp "lib/jade.jar" src/* -d bin/

clean:
	rm bin/*

gui:
	java -cp "lib/jade.jar:bin" jade.Boot -gui

container:
	java -cp "lib/jade.jar:bin" jade.Boot -container

agent:
	java -cp "lib/jade.jar:bin" jade.Boot -agents "bank:Bank;stock:StockMarket;oracle:Oracle;a1:NormalAgent;a2:NormalAgent" -gui
