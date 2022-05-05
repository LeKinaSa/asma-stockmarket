package stockmarket;

import java.util.HashMap;
import java.util.Map;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.protocols.RequestResponder;
import stockmarket.behaviours.RequestResponderBehaviour;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class Bank extends Agent {
    private Map<String, Double> bankAccount = new HashMap<>();

    private class BankBalanceQuery extends RequestResponder {
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
                    if (!bankAccount.containsKey(agent)) {
                        double value = 0D;
                        try {
                            value = Double.parseDouble(action.getInformation());
                        }
                        catch (NumberFormatException ignored) {}
                        bankAccount.put(agent, value);
                        return "Started Bank Account";
                    }
                    return bankAccount.get(agent).toString();
                }
                case CHECK_BALANCE: {
                    return bankAccount.get(agent).toString();
                }
                default: {
                    return "Action Not Supported";
                }
            }
        }
    }

    public void setup() {
		Utils.log(this, "Ready");

        BankBalanceQuery responder = new BankBalanceQuery();
        addBehaviour(new RequestResponderBehaviour(this, responder, responder.getTemplate()));
    }
}
