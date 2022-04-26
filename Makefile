all: clean compile start

compile:
	./gradlew build
	mkdir bin
	cp -r app/build/classes/java/main/* bin

clean:
	rm -r app/build bin

start2:
	java -cp "app/lib/jade.jar:bin" jade.Boot -agents "bank:stockmarket.Bank;stock:stockmarket.StockMarket;oracle:stockmarket.Oracle;a1:stockmarket.NormalAgent;a2:stockmarket.NormalAgent" -gui

start:
	java -cp "app/lib/jade.jar:bin" jade.Boot -agents "bank:stockmarket.Bank"
