package stockmarket.utils;

import java.util.List;

import com.google.gson.Gson;

public class StockMarketEntry {
    public List<Stock> ownedStocks;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
