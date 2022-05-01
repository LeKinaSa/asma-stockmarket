package stockmarket;

import java.util.HashMap;
import java.util.Map;

import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import stockmarket.agents.RequestResponder;
import stockmarket.behaviors.RequestResponderBehavior;
import stockmarket.utils.Utils;

public class StockMarket extends Agent {
	private Map<String, String> stockMarket = new HashMap<>();

	private class StockMarketQuery implements RequestResponder {
        @Override
        public boolean checkAction(ACLMessage request) {
            return true;
        }

        @Override
        public String performAction(ACLMessage request) {
            String agent = request.getSender().getLocalName();
            if (!stockMarket.containsKey(agent)) {
                stockMarket.put(agent, "{}");
                return "Started Stock Market Entry";
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
