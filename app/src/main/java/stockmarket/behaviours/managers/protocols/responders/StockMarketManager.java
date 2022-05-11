package stockmarket.behaviours.managers.protocols.responders;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.StockMarketAgent;
import stockmarket.behaviours.RequestInitiatorBehaviour;
import stockmarket.behaviours.managers.messages.NewDayListener;
import stockmarket.behaviours.managers.protocols.initiators.RequestInitiator;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class StockMarketManager extends RequestResponder {
    private static final Gson gson = new Gson();
    private static final File file = new File("data/formatted_stock_prices.json");
    private final Map<String, Map<String, Integer>> stockMarketEntries = new HashMap<>();
    private final Map<String, Map<String, Double >> stockPrices;
    
    private final StockMarketAgent    stockMarketAgent;
    private final NewDayListener newDayListener;

    public StockMarketManager(StockMarketAgent stockMarketAgent, NewDayListener newDayListener) {
        this.stockMarketAgent = stockMarketAgent;
        this.newDayListener = newDayListener;
        this.stockPrices = loadStockPrices(stockMarketAgent);
    }

    @Override
    public String performAction(ACLMessage request) {
        Action action = Action.toAction(request.getOntology(), request.getContent());
        String agent = request.getSender().getLocalName();
        if (action == null) {
            return Utils.invalidAction("");
        }

        ActionType actionType = action.getType();
        switch (actionType) {
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
                    if (!stockMarketEntries.containsKey(agent)) {
                        stockMarketEntries.put(agent, entry);
                        return "Started Stock Market Entry with " + entry + ".";
                    }
                }
            }
            case CHECK_OWNED_STOCK: {
                Map<String, Integer> entry;
                synchronized (stockMarketEntries) {
                    entry = stockMarketEntries.get(agent);
                }
                return "Owned Stocks: " + gson.toJson(entry) + ".";
            }
            case CHECK_STOCK_PRICES: {
                return "Current Stock Prices (day " + newDayListener.getDay() + "): " + gson.toJson(getDailyStocks()) + ".";
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
                    stockEntry = stockMarketEntries.get(agent);
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

                String bankMessage = waitResponse(total);
                if (bankMessage == null) {
                    return Utils.invalidAction("Invalid Answer from the Bank");
                }
                if (bankMessage.startsWith("Invalid Action")) {
                    return Utils.invalidAction("Bank Denied with Error \"" + bankMessage + "\"");
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

    public Map<String, Double> getDailyStocks() {
        return stockPrices.get(String.valueOf(newDayListener.getDay()));
    }

    public double getDailyPrice(String stock) {
        return getDailyStocks().get(stock);
    }

    public String waitResponse(double total) {
        Set<String> receivers = stockMarketAgent.getBankAgents();
        Action action = new Action(ActionType.MANAGE_MONEY, String.valueOf(total));
        RequestInitiator initiator = new RequestInitiator(receivers, action);
        stockMarketAgent.addBehaviour(new RequestInitiatorBehaviour(stockMarketAgent, initiator));
        return null; // TODO: obtain answer somehow
    }

    public static Map<String, Map<String, Double>> loadStockPrices(Agent agent) {
        Map<String, Map<String, Double >> stocks = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            stocks = gson.fromJson(reader, Map.class);
        }
        catch (JsonSyntaxException | IOException exception) {
            Utils.log(agent, "Error when loading the stockmarket prices history -> " + exception.getMessage());
        }
        return stocks;
    }
}