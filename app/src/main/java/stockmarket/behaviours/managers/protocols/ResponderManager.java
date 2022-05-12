package stockmarket.behaviours.managers.protocols;

import java.util.HashMap;
import java.util.Map;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.EnvironmentAgent;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.MoneyTransfer;
import stockmarket.utils.Utils;

public class ResponderManager extends RequestResponder {
    private final Map<String, Map<String, Double >> stockPrices;
    private final Map<String, Map<String, Integer>> stockMarketEntries = new HashMap<>();
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
                Map<String, Integer> entry = Utils.getSingleMapFromJson(action.getInformation());
                if (entry == null) {
                    entry = new HashMap<>();
                }

                for (String stock : entry.keySet()) {
                    if (entry.get(stock) < 0) {
                        entry.put(stock, 0);
                    }
                }

                synchronized (stockMarketEntries) {
                    if (!stockMarketEntries.containsKey(agentName)) {
                        stockMarketEntries.put(agentName, entry);
                        return "Started Stock Market Entry with " + entry + ".";
                    }
                }
            }
            case CHECK_OWNED_STOCK: {
                Map<String, Integer> entry;
                synchronized (stockMarketEntries) {
                    entry = stockMarketEntries.get(agentName);
                }
                return "Owned Stocks: " + Utils.gson.toJson(entry) + ".";
            }
            case CHECK_STOCK_PRICES: {
                return "Current Stock Prices (day " + agent.getDay() + "): " + Utils.gson.toJson(getDailyStocks()) + ".";
            }
            case BUY_SELL_STOCK: {
                Map<String, Integer> exchangeEntry = Utils.getSingleMapFromJson(action.getInformation());
                if (exchangeEntry == null) {
                    return Utils.invalidAction("Invalid Stock Exchange");
                }

                // Check Request Validity
                Map<String, Integer> stockEntry;
                synchronized (stockMarketEntries) {
                    stockEntry = stockMarketEntries.get(agentName);
                }
                Integer amount;
                for (String stock : exchangeEntry.keySet()) {
                    amount = exchangeEntry.get(stock);
                    if (amount == null) {
                        // Amount can't be null
                        return Utils.invalidAction("Invalid Amount");
                    }

                    if (!stockEntry.containsKey(stock) && amount < 0) {
                        // Amount must be positive when agent doesn't own any stocks of that type
                        return Utils.invalidAction("No Stock to Sell");
                    }

                    if (!getDailyStocks().containsKey(stock)) {
                        // Stock doesn't exist
                        return Utils.invalidAction("Invalid stock");
                    }
                }

                double total = 0D;
                for (String stock : exchangeEntry.keySet()) {
                    amount = exchangeEntry.get(stock);
                    total += amount * getDailyPrice(stock);
                }

                MoneyTransfer transfer = new MoneyTransfer(agentName, total);
                synchronized (bankAccount) {
                    if (!bankAccount.containsKey(transfer.getTo())) {
                        return Utils.invalidAction("Unknown Agent is Managing the Stocks");
                    }
                    if (transfer.getAmount() < 0 && -transfer.getAmount() > bankAccount.get(transfer.getTo())) {
                        return Utils.invalidAction("Not Enough Money for These Stocks");
                    }
                    bankAccount.put(agentName, bankAccount.get(transfer.getTo()) + transfer.getAmount());
                }

                int newAmount, oldAmount;
                synchronized (stockMarketEntries) {
                    for (String stock : exchangeEntry.keySet()) {
                        amount = exchangeEntry.get(stock);
                        oldAmount = (!stockEntry.containsKey(stock)) ? 0 : stockEntry.get(stock); 
                        newAmount = oldAmount + amount;
                        stockEntry.put(stock, newAmount);
                    }
                }

                return "Stock Exchange Completed with Success.";
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
