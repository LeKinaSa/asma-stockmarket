package stockmarket.listeners.protocols;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jade.lang.acl.ACLMessage;
import stockmarket.StockMarket;
import stockmarket.behaviours.RequestInitiatorBehaviour;
import stockmarket.listeners.messages.NewDayListener;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.StockMarketEntry;
import stockmarket.utils.Utils;

public class StockMarketManager extends RequestResponder {
    private class StockMarketAgentEntry extends StockMarketEntry<Integer> {}
    private class StockMarketPriceEntry extends StockMarketEntry<Double> {}

    private Gson gson = new Gson();
    private Map<String, StockMarketAgentEntry> stockMarketEntries = new HashMap<>();
    private Map<Integer, Map<String, StockMarketPriceEntry>> stockPrices = new HashMap<>();
    
    private final StockMarket stockMarketAgent;
    private final NewDayListener newDayListener;

    public StockMarketManager(StockMarket stockMarketAgent, NewDayListener newDayListener) {
        this.stockMarketAgent = stockMarketAgent;
        this.newDayListener = newDayListener;
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
                StockMarketAgentEntry entry = new StockMarketAgentEntry(); // TODO: use stock prices per day
                return "Current Stock Prices (day " + newDayListener.getDay() + "): " + entry + ".";
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
                for (String stock : exchangeEntry.stocks.keySet()) {
                    amount = exchangeEntry.stocks.get(stock);
                    if (amount == null) {
                        // Amount can't be null
                        return Utils.invalidAction("Invalid Amount");
                    }

                    if (!stockEntry.stocks.containsKey(stock) && amount < 0) {
                        // Amount must be positive when agent doesn't own any stocks of that type
                        return Utils.invalidAction("No Stock to Sell");
                    }

                    if (!getDailyStocks().keySet().contains(stock)) {
                        // Stock doesn't exist
                        return Utils.invalidAction("Invalid stock");
                    }
                }

                double total = 0D;
                for (String stock : exchangeEntry.stocks.keySet()) {
                    amount = exchangeEntry.stocks.get(stock);
                    total += amount * getDailyPrice(agent, stock);
                }

                String bankMessage = waitResponse(total);
                if (response == null) {
                    return Utils.invalidAction("Invalid Answer from the Bank");
                }
                if (response.getContent().startsWith("Invalid Action")) {
                    return Utils.invalidAction("Bank Denied with Error \"" + response + "\"");
                }

                int newAmount, oldAmount;
                // TODO: lock stock
                for (String stock : exchangeEntry.stocks.keySet()) {
                    amount = exchangeEntry.stocks.get(stock);
                    oldAmount = (!stockEntry.stocks.containsKey(stock)) ? 0 : stockEntry.stocks.get(stock); 
                    newAmount = oldAmount + amount;
                    stockEntry.stocks.put(stock, newAmount);
                }
                // TODO: unlock stock

                return "Stock Exchange Completed with Success.";
            }
            default: {
                return Utils.invalidAction("Action Not Supported");
            }
        }
    }

    public Map<String, StockMarketPriceEntry> getDailyStocks() {
        return stockPrices.get(newDayListener.getDay());
    }

    public double getDailyPrice(String agent, String stock) {
        return getDailyStocks().get(agent).stocks.get(stock);
    }

    public String waitResponse(double total) {
        List<String> receivers = stockMarketAgent.getBankAgents();
        Action action = new Action(ActionType.MANAGE_MONEY, String.valueOf(total));
        RequestInitiator initiator = new RequestInitiator();
        stockMarketAgent.addBehaviour(
            new RequestInitiatorBehaviour(
                stockMarketAgent, initiator, initiator.getMessage(receivers, action), receivers.size()
            )
        );
        return null; // TODO: obtain answer somehow
    }
}
