all: clean compile start

compile:
	./gradlew build
	mkdir bin
	cp -r app/build/classes/java/main/* bin

clean:
	rm -r app/build bin

start:
	java -cp "app/lib/gson.jar:app/lib/jade.jar:bin" jade.Boot -agents "time:stockmarket.TimeManager;bank:stockmarket.Bank;stockmarket:stockmarket.StockMarket;oracle:stockmarket.Oracle;a1:stockmarket.NormalAgent;a2:stockmarket.NormalAgent" -gui
