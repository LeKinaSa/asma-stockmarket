package stockmarket.listeners.protocols;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import jade.domain.FIPAAgentManagement.FailureException;
import jade.lang.acl.ACLMessage;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.MoneyTransfer;

public class BankManager extends RequestResponder {
    private Gson gson = new Gson();
    private Map<String, Double> bankAccount = new HashMap<>();

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
                double value = 0D;
                try {
                    value = Double.parseDouble(action.getInformation());
                }
                catch (NumberFormatException ignored) {}

                // TODO: lock bank
                if (!bankAccount.containsKey(agent)) {
                    bankAccount.put(agent, value);

                    // TODO: unlock bank
                    return "Started Bank Account with " + value + ".";
                }
                // TODO: unlock bank
            }
            case CHECK_BALANCE: {
                // TODO: lock bank
                Double balance = bankAccount.get(agent);
                // TODO: unlock bank

                return "Balance is at " + balance + ".";
            }
            case TRANSFER_MONEY: {
                MoneyTransfer transfer;
                try {
                    transfer = gson.fromJson(action.getInformation(), MoneyTransfer.class);
                }
                catch (JsonSyntaxException e) {
                    return "Invalid Action: Invalid Transfer";
                }

                // TODO: lock bank
                if (!bankAccount.containsKey(transfer.to)) {
                    // TODO: unlock bank
                    return "Invalid Action: Unknown Transfer Destination Agent";
                }
                if (bankAccount.get(agent) < transfer.amount) {
                    // TODO: unlock bank
                    return "Invalid Action: Not Enough Money for Transfer";
                }
                bankAccount.put(agent, bankAccount.get(agent) - transfer.amount);
                bankAccount.put(agent, bankAccount.get(transfer.to) + transfer.amount);
                
                double balance = bankAccount.get(agent);
                // TODO: unlock bank

                return transfer.amount + " transfered. Balance is now at " + balance + ".";
            }
            default: {
                return "Invalid Action: Action Not Supported";
            }
        }
    }
}
