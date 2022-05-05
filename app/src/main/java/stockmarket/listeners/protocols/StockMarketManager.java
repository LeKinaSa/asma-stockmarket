package stockmarket.listeners.protocols;

import java.util.HashMap;
import java.util.Map;
import jade.lang.acl.ACLMessage;
import stockmarket.listeners.messages.NewDayListener;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;

public class StockMarketManager extends RequestResponder {
	private Map<String, String> stockMarket = new HashMap<>();
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
            return "Invalid Action";
        }

        ActionType actionType = action.getType();
        switch (actionType) {
            case START: {
                if (!stockMarket.containsKey(agent)) {
                    stockMarket.put(agent, action.getInformation());
                    return "Started Stock Market Entry";
                }
                return stockMarket.get(agent).toString();
            }
            case CHECK_STOCK: {
                return stockMarket.get(agent).toString();
            }
            default: {
                return "Action Not Supported " + newDayListener.getDay();
            }
        }
    }
}
