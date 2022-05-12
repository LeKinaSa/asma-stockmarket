package stockmarket.utils;

public class StockEntry {
    private final String company;
    private final int stocks;

    public StockEntry(String company, int stocks) {
        this.company = company;
        this.stocks = stocks;
    }

    public String getCompany() {
        return company;
    }

    public int getStocks() {
        return stocks;
    }

    @Override
    public String toString() {
        return Utils.gson.toJson(this);
    }
}
