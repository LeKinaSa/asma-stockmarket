package stockmarket;

import java.util.HashMap;
import java.util.Map;

import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import stockmarket.agents.NewDayListener;
import stockmarket.agents.RequestResponder;
import stockmarket.behaviors.ListeningBehavior;
import stockmarket.behaviors.RequestResponderBehavior;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class StockMarket extends Agent {
	private Map<String, String> stockMarket = new HashMap<>();
    private NewDayListener newDayListener = new NewDayListener();

	private class StockMarketQuery implements RequestResponder {
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
                    return "Action Not Supported";
                }
            }
        }
    }

	public void setup() {
		Utils.log(this, "Ready");

        MessageTemplate template = Utils.getMessageTemplate(
            FIPANames.InteractionProtocol.FIPA_REQUEST, ACLMessage.REQUEST
        );

        addBehaviour(new RequestResponderBehavior(this, new StockMarketQuery(), template));
        addBehaviour(new ListeningBehavior(this, newDayListener));
	}
}
