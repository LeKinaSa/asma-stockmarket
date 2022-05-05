package stockmarket.utils;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;

public class StockMarketEntry<T> {
    public Map<String, T> stocks = new HashMap<>();

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
