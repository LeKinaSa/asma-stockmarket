package stockmarket;

import java.util.HashMap;
import java.util.Map;

import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import stockmarket.agents.RequestResponder;
import stockmarket.behaviors.RequestResponderBehavior;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class StockMarket extends Agent {
	private Map<String, String> stockMarket = new HashMap<>();

	private class StockMarketQuery implements RequestResponder {
        @Override
        public boolean checkAction(ACLMessage request) {
            Action action = Action.toAction(request.getContent());
            if (action == null) {
                return false;
            }
            ActionType type = action.getType();
            return type.equals(ActionType.START) || type.equals(ActionType.CHECK_STOCK) || type.equals(ActionType.BUY_STOCK);
        }

        @Override
        public String performAction(ACLMessage request) {
            Action action = Action.toAction(request.getContent());
            String agent = request.getSender().getLocalName();

            if (action.getType().equals(ActionType.START)) {
                if (!stockMarket.containsKey(agent)) {
                    stockMarket.put(agent, "{}");
                    return "Started Stock Market Entry";
                }
            }

            if (action.getType().equals(ActionType.BUY_STOCK)) {
                return "Not Yet Implemented"; // TODO
            }

            return stockMarket.get(agent).toString();
        }
    }

	public void setup() {
		Utils.log(this, "Ready");

        MessageTemplate template = Utils.getMessageTemplate(
            FIPANames.InteractionProtocol.FIPA_REQUEST, ACLMessage.REQUEST
        );

        addBehaviour(new RequestResponderBehavior(this, new StockMarketQuery(), template));
	}
}
