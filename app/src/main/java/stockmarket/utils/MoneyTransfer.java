package stockmarket.utils;

import com.google.gson.Gson;

public class MoneyTransfer {
    private String to;
    private double amount;

    public MoneyTransfer(String to, double amount) {
        this.to = to;
        this.amount = amount;
    }

    public String getTo() {
        return to;
    }

    public double getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
