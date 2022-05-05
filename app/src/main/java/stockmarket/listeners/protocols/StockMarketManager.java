package stockmarket.listeners.protocols;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jade.lang.acl.ACLMessage;
import stockmarket.listeners.messages.NewDayListener;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.StockMarketEntry;
import stockmarket.utils.Utils;

public class StockMarketManager extends RequestResponder {
    private Gson gson = new Gson();
	private Map<String, StockMarketEntry> stockMarket = new HashMap<>();
    private final NewDayListener newDayListener;

    public StockMarketManager(NewDayListener newDayListener) {
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
            case START: {
                StockMarketEntry entry = new StockMarketEntry();
                try {
                    entry = gson.fromJson(action.getInformation(), StockMarketEntry.class);
                }
                catch (JsonSyntaxException e) {}

                // TODO: lock stock
                if (!stockMarket.containsKey(agent)) {
                    stockMarket.put(agent, entry);

                    // TODO: unlock stock
                    return "Started Stock Market Entry with " + entry + ".";
                }
                // TODO: unlock stock
            }
            case CHECK_OWNED_STOCK: {
                // TODO: lock stock
                StockMarketEntry entry = stockMarket.get(agent);
                // TODO: unlock stock

                return "Owned Stocks: " + entry + ".";
            }
            case CHECK_STOCK_PRICES: {
                StockMarketEntry entry = new StockMarketEntry(); // TODO: use stock prices per day
                return "Current Stock Prices (day " + newDayListener.getDay() + "): " + entry + ".";
            }
            case BUY_STOCK: {
                // TODO
            }
            default: {
                return Utils.invalidAction("Action Not Supported");
            }
        }
    }
}
