package stockmarket.behaviours.managers.protocols;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.StockMarketAgent;
import stockmarket.behaviours.RequestInitiatorBehaviour;
import stockmarket.behaviours.managers.messages.NewDayListener;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.StockMarketEntry;
import stockmarket.utils.Utils;

public class StockMarketManager extends RequestResponder {
    public class StockMarketAgentEntry extends StockMarketEntry<Integer> {}
    public class StockMarketPriceEntry extends StockMarketEntry<Double> {}

    private static final Gson gson = new Gson();
    private static final File file = new File("data/formatted_stock_prices.json");
    private final Map< String, StockMarketAgentEntry> stockMarketEntries = new HashMap<>();
    private final Map<Integer, StockMarketPriceEntry> stockPrices;
    
    private final StockMarketAgent    stockMarketAgent;
    private final NewDayListener newDayListener;

    public StockMarketManager(StockMarketAgent stockMarketAgent, NewDayListener newDayListener) {
        this.stockMarketAgent = stockMarketAgent;
        this.newDayListener = newDayListener;
        this.stockPrices = loadStockPrices(stockMarketAgent);
    }

    @Override
    public boolean checkAction(ACLMessage request) {
        Action action = Action.toAction(request.getOntology(), request.getContent());
        return action != null;
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
            case START: { // TODO: it is possible to have negative stock
                StockMarketAgentEntry entry = new StockMarketAgentEntry();
                try {
                    entry = gson.fromJson(action.getInformation(), StockMarketAgentEntry.class);
                }
                catch (JsonSyntaxException e) {}

                // TODO: lock stock
                if (!stockMarketEntries.containsKey(agent)) {
                    stockMarketEntries.put(agent, entry);

                    // TODO: unlock stock
                    return "Started Stock Market Entry with " + entry + ".";
                }
                // TODO: unlock stock
            }
            case CHECK_OWNED_STOCK: {
                // TODO: lock stock
                StockMarketAgentEntry entry = stockMarketEntries.get(agent);
                // TODO: unlock stock

                return "Owned Stocks: " + entry + ".";
            }
            case CHECK_STOCK_PRICES: {
                return "Current Stock Prices (day " + newDayListener.getDay() + "): " + getDailyStocks() + ".";
            }
            case BUY_STOCK: {
                StockMarketAgentEntry exchangeEntry;
                try {
                    exchangeEntry = gson.fromJson(action.getInformation(), StockMarketAgentEntry.class);
                }
                catch (JsonSyntaxException e) {
                    return Utils.invalidAction("Invalid Stock Exchange");
                }

                // Check Request Validity
                StockMarketAgentEntry stockEntry = stockMarketEntries.get(agent);
                Integer amount;
                for (String stock : exchangeEntry.keys()) {
                    amount = exchangeEntry.get(stock);
                    if (amount == null) {
                        // Amount can't be null
                        return Utils.invalidAction("Invalid Amount");
                    }

                    if (!stockEntry.contains(stock) && amount < 0) {
                        // Amount must be positive when agent doesn't own any stocks of that type
                        return Utils.invalidAction("No Stock to Sell");
                    }

                    if (!getDailyStocks().contains(stock)) {
                        // Stock doesn't exist
                        return Utils.invalidAction("Invalid stock");
                    }
                }

                double total = 0D;
                for (String stock : exchangeEntry.keys()) {
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
                // TODO: lock stock
                for (String stock : exchangeEntry.keys()) {
                    amount = exchangeEntry.get(stock);
                    oldAmount = (!stockEntry.contains(stock)) ? 0 : stockEntry.get(stock); 
                    newAmount = oldAmount + amount;
                    stockEntry.put(stock, newAmount);
                }
                // TODO: unlock stock

                return "Stock Exchange Completed with Success.";
            }
            default: {
                return Utils.invalidAction("Action Not Supported");
            }
        }
    }

    public StockMarketPriceEntry getDailyStocks() {
        return stockPrices.get(newDayListener.getDay());
    }

    public double getDailyPrice(String stock) {
        return getDailyStocks().get(stock);
    }

    public String waitResponse(double total) {
        List<String> receivers = stockMarketAgent.getBankAgents();
        Action action = new Action(ActionType.MANAGE_MONEY, String.valueOf(total));
        RequestInitiator initiator = new RequestInitiator(receivers, action);
        stockMarketAgent.addBehaviour(new RequestInitiatorBehaviour(stockMarketAgent, initiator));
        return null; // TODO: obtain answer somehow
    }

    public static Map<Integer, StockMarketPriceEntry> loadStockPrices(Agent agent) {
        Map<Integer, StockMarketPriceEntry> stocks = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            stocks = gson.fromJson(reader, Map.class);
        }
        catch (JsonSyntaxException | IOException exception) {
            Utils.log(agent, "Error when loading the stockmarket prices history -> " + exception.getMessage());
        }
        return stocks;
    }
}
