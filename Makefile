all: clean compile start

compile:
	./gradlew build
	mkdir bin
	cp -r app/build/classes/java/main/* bin

clean:
	rm -r app/build bin

start:
	java -cp "app/lib/gson.jar:app/lib/jade.jar:bin" jade.Boot -agents \
	"time:stockmarket.agents.TimeManagerAgent; \
	bank:stockmarket.agents.BankAgent; \
	stockmarket:stockmarket.agents.StockMarketAgent; \
	oracle:stockmarket.agents.OracleAgent; \
	a1:stockmarket.agents.NormalAgent; \
	a2:stockmarket.agents.NormalAgent" \
	-gui
