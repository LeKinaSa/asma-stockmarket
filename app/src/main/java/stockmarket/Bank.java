package stockmarket;

import java.util.HashMap;
import java.util.Map;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.FIPANames;

import stockmarket.agents.RequestResponder;
import stockmarket.behaviors.RequestResponderBehavior;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class Bank extends Agent {
    private Map<String, Double> bankAccount = new HashMap<>();

    private class BankBalanceQuery implements RequestResponder {
        @Override
        public boolean checkAction(ACLMessage request) {
            Action action = Action.toAction(request.getContent());
            if (action == null) {
                return false;
            }
            ActionType type = action.getType();
            return type.equals(ActionType.START) || type.equals(ActionType.CHECK_BALANCE);
        }

        @Override
        public String performAction(ACLMessage request) {
            Action action = Action.toAction(request.getContent());
            String agent = request.getSender().getLocalName();

            if (action.getType().equals(ActionType.START)) {
                if (!bankAccount.containsKey(agent)) {
                    bankAccount.put(agent, 0D);
                    return "Started Bank Account";
                }
            }

            return bankAccount.get(agent).toString();
        }
    }

    public void setup() {
		Utils.log(this, "Ready");

        MessageTemplate template = Utils.getMessageTemplate(
            FIPANames.InteractionProtocol.FIPA_REQUEST, ACLMessage.REQUEST
        );

        addBehaviour(new RequestResponderBehavior(this, new BankBalanceQuery(), template));
    }
}
