package stockmarket.utils;

public class Loan {
    private final double profit;
    private double amount;

    public Loan(double profit, double amount) {
        this.profit = profit;
        this.amount = amount;
    }

    public Loan() {
        this.profit = 0;
        this.amount = 0;
    }

    public double getProfit() {
        return profit;
    }

    public double getAmount() {
        return amount;
    }

    public void deny() {
        amount = 0;
    }

    @Override
    public String toString() {
        return Utils.gson.toJson(this);
    }
}
