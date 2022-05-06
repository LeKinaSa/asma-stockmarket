package stockmarket.utils;

import com.google.gson.Gson;

public class MoneyTransfer {
    public String to;
    public double amount;

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
