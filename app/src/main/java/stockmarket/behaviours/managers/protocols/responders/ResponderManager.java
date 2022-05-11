package stockmarket.behaviours.managers.protocols.responders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.EnvironmentAgent;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.MoneyTransfer;
import stockmarket.utils.Utils;

public class ResponderManager extends RequestResponder {
    private static final Gson gson = new Gson();
    private static final File file = new File("data/formatted_stock_prices.json");
    private final Map<String, Map<String, Double >> stockPrices;
    private final Map<String, Map<String, Integer>> stockMarketEntries = new HashMap<>();
    private final Map<String, Double> bankAccount = new HashMap<>();
    private final EnvironmentAgent agent;

    public ResponderManager(EnvironmentAgent agent) {
        this.agent = agent;
        this.stockPrices = loadStockPrices();
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
                MoneyTransfer transfer = null;
                try {
                    transfer = gson.fromJson(action.getInformation(), MoneyTransfer.class);
                }
                catch (JsonSyntaxException e) {
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
                Map<String, Integer> entry = new HashMap<>();
                try {
                    entry = gson.fromJson(action.getInformation(), Map.class);
                }
                catch (JsonSyntaxException e) {}

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
                return "Owned Stocks: " + gson.toJson(entry) + ".";
            }
            case CHECK_STOCK_PRICES: {
                return "Current Stock Prices (day " + agent.getDay() + "): " + gson.toJson(getDailyStocks()) + ".";
            }
            case BUY_STOCK: {
                Map<String, Integer> exchangeEntry;
                try {
                    exchangeEntry = gson.fromJson(action.getInformation(), Map.class);
                }
                catch (JsonSyntaxException e) {
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

    public Map<String, Map<String, Double>> loadStockPrices() {
        Map<String, Map<String, Double >> stocks = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            stocks = gson.fromJson(reader, Map.class);
        }
        catch (JsonSyntaxException | IOException exception) {
            Utils.log(agent, "Error when loading the stockmarket prices history -> " + exception.getMessage());
        }
        return stocks;
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
