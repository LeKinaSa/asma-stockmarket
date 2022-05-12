package stockmarket.utils;

public class Loan {
    private final int day;
    private final double profit;
    private double amount;

    public Loan(int day, double amount, double profit) {
        this.day = day;
        this.amount = amount;
        this.profit = profit;
    }

    public Loan() {
        this.day = 0;
        this.amount = 0;
        this.profit = 0;
    }

    public int getDay() {
        return day;
    }

    public double getAmount() {
        return amount;
    }

    public double getProfit() {
        return profit;
    }

    public void deny() {
        amount = 0;
    }
}
