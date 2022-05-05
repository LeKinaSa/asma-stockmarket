package stockmarket.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;

public class StockMarketEntry<T> {
    public Map<String, T> stocks = new HashMap<>();

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public boolean contains(String stockName) {
        return stocks.containsKey(stockName);
    }

    public T get(String stockName) {
        return stocks.get(stockName);
    }

    public void put(String stockName, T amountOrPrice) {
        stocks.put(stockName, amountOrPrice);
    }

    public Set<String> keys() {
        return stocks.keySet();
    }
}
