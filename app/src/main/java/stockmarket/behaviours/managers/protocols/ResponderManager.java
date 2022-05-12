package stockmarket.behaviours.managers.protocols;

import java.util.HashMap;
import java.util.Map;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.EnvironmentAgent;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.MoneyTransfer;
import stockmarket.utils.StockEntry;
import stockmarket.utils.Utils;

public class ResponderManager extends RequestResponder {
    private final Map<String, Map<String, Double>> stockPrices;
    private final Map<String, StockEntry> stockMarketEntries = new HashMap<>();
    private final Map<String, Double> bankAccount = new HashMap<>();
    private final EnvironmentAgent agent;

    public ResponderManager(EnvironmentAgent agent) {
        this.agent = agent;
        this.stockPrices = Utils.loadStockPrices(agent);
    }

    @Override
    public String performAction(ACLMessage request) {
        Action action = Action.toAction(request.getOntology(), request.getContent());
        String agentName = request.getSender().getLocalName();
        if (action == null) {
            return Utils.invalidAction("");
        }

        ActionType actionType = action.getType();
        switch (actionType) {
            case START_BANK: {
                double value = 0D;
                try {
                    value = Double.parseDouble(action.getInformation());
                }
                catch (NumberFormatException ignored) {}

                synchronized (bankAccount) {
                    if (!bankAccount.containsKey(agentName)) {
                        bankAccount.put(agentName, value);
                        return "Started Bank Account with " + value + ".";
                    }
                }
            }
            case CHECK_BALANCE: {
                double balance;
                synchronized (bankAccount) {
                    balance = bankAccount.get(agentName);
                }
                return "Balance is at " + balance + ".";
            }
            case TRANSFER_MONEY: {
                MoneyTransfer transfer = Utils.getTransferFromJson(action.getInformation());
                if (transfer == null) {
                    return Utils.invalidAction("Invalid Transfer");
                }

                double balance;
                synchronized (bankAccount) {
                    if (!bankAccount.containsKey(transfer.getTo())) {
                        return Utils.invalidAction("Unknown Transfer Destination Agent");
                    }
                    if (bankAccount.get(agentName) < transfer.getAmount()) {
                        return Utils.invalidAction("Not Enough Money for Transfer");
                    }
                    bankAccount.put(agentName, bankAccount.get(agentName)       - transfer.getAmount());
                    bankAccount.put(agentName, bankAccount.get(transfer.getTo()) + transfer.getAmount());

                    balance = bankAccount.get(agentName);
                }

                return transfer.getAmount() + " transfered. Balance is now at " + balance + ".";
            }
            case START_STOCK: {
                StockEntry entry = Utils.getStockEntryFromJson(action.getInformation());
                if (entry == null) {
                    return  "Start Empty Stock Market Entry";
                }

                synchronized (stockMarketEntries) {
                    if (!stockMarketEntries.containsKey(agentName)) {
                        stockMarketEntries.put(agentName, entry);
                        return "Started Stock Market Entry with " + entry + ".";
                    }
                }
            }
            case CHECK_OWNED_STOCK: {
                StockEntry entry;
                synchronized (stockMarketEntries) {
                    entry = stockMarketEntries.get(agentName);
                }
                return "Owned Stocks: " + entry.toString() + ".";
            }
            case CHECK_STOCK_PRICES: {
                return "Current Stock Prices (day " + agent.getDay() + "): " + Utils.gson.toJson(getDailyStocks()) + ".";
            }
            case BUY_STOCK: {
                // Buy Max Possible Stocks
                String company = request.getContent();
                Double price = getDailyPrice(company);
                if (price <= 0) {
                    return Utils.invalidAction("Invalid Stock Price");
                }

                Double money;
                int buy;
                StockEntry stockEntry;
                double rest;
                synchronized (bankAccount) {
                    money = bankAccount.get(agentName);
                    if (money == null) {
                        return Utils.invalidAction("Agent doesn't have a Bank Account.");
                    }
                    buy = (int) (money / price);
                    rest = money - buy * price;

                    bankAccount.put(agentName, rest);
                }

                stockEntry = new StockEntry(company, buy);
                synchronized (stockMarketEntries) {
                    stockMarketEntries.put(agentName, stockEntry);
                }

                return "Buy Stocks Completed with Success.";
            }
            case SELL_STOCK: {
                // Sell Owned Stocks
                StockEntry stockEntry;
                synchronized (stockMarketEntries) {
                    stockEntry = stockMarketEntries.get(agentName);
                }
                if (stockEntry == null) {
                    return "No Stocks to Sell.";
                }

                Double price = getDailyPrice(stockEntry.getCompany());
                if (price <= 0) {
                    return Utils.invalidAction("Invalid Stock Price");
                }

                Double total = price * stockEntry.getStocks();
                Double balance;
                synchronized (bankAccount) {
                    balance = bankAccount.get(agentName);
                    if (balance == null) {
                        return Utils.invalidAction("Agent doesn't have a Bank Account.");
                    }
                    bankAccount.put(agentName, balance + total);
                }

                return "Sell Stocks Completed with Success.";
            }
            default: {
                return Utils.invalidAction("Action Not Supported");
            }
        }
    }

    public Map<String, Map<String, Double>> getStockPrices() {
        return stockPrices;
    }
    
    public Map<String, Double> getDailyStocks() {
        return stockPrices.get(String.valueOf(agent.getDay()));
    }

    public double getDailyPrice(String stock) {
        return getDailyStocks().get(stock);
    }
}
