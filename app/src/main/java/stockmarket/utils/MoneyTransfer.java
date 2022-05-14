package stockmarket.utils;

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
        return Utils.gson.toJson(this);
    }
}
