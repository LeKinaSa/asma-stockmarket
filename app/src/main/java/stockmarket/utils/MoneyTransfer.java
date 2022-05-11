package stockmarket.utils;

import com.google.gson.Gson;

public class MoneyTransfer {
    private String to;
    private double amount;

    public MoneyTransfer(String to, double amount) {
        this.setTo(to);
        this.setAmount(amount);
    }

    public String getTo() {
        return to;
    }

    public double getAmount() {
        return amount;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
