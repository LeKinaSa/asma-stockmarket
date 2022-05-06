package stockmarket.behaviours.managers.protocols;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jade.lang.acl.ACLMessage;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.MoneyTransfer;
import stockmarket.utils.Utils;

public class BankManager extends RequestResponder {
    private final static Gson gson = new Gson();
    private final Map<String, Double> bankAccount = new HashMap<>();
    private final List<String> stockAgents = Arrays.asList("stockmarket");

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
                double balance = bankAccount.get(agent);
                // TODO: unlock bank

                return "Balance is at " + balance + ".";
            }
            case TRANSFER_MONEY: {
                MoneyTransfer transfer = null;
                try {
                    transfer = gson.fromJson(action.getInformation(), MoneyTransfer.class);
                }
                catch (JsonSyntaxException e) {
                    return Utils.invalidAction("Invalid Transfer");
                }

                // TODO: lock bank
                if (!bankAccount.containsKey(transfer.to)) {
                    // TODO: unlock bank
                    return Utils.invalidAction("Unknown Transfer Destination Agent");
                }
                if (bankAccount.get(agent) < transfer.amount) {
                    // TODO: unlock bank
                    return Utils.invalidAction("Not Enough Money for Transfer");
                }
                bankAccount.put(agent, bankAccount.get(agent)       - transfer.amount);
                bankAccount.put(agent, bankAccount.get(transfer.to) + transfer.amount);

                double balance = bankAccount.get(agent);
                // TODO: unlock bank

                return transfer.amount + " transfered. Balance is now at " + balance + ".";
            }
            case MANAGE_MONEY: {
                MoneyTransfer transfer = new MoneyTransfer();
                try {
                    transfer = gson.fromJson(action.getInformation(), MoneyTransfer.class);
                }
                catch (JsonSyntaxException e) {
                    return Utils.invalidAction("Invalid Transfer");
                }

                if (!stockAgents.contains(agent)) {
                    return Utils.invalidAction("Only the Stock Market can Perform this Action");
                }

                // TODO: lock bank
                if (!bankAccount.containsKey(transfer.to)) {
                    // TODO: unlock bank
                    return Utils.invalidAction("Unknown Agent is Managing the Stocks");
                }
                if (transfer.amount < 0 && -transfer.amount > bankAccount.get(transfer.to)) {
                    // TODO: unlock bank
                    return Utils.invalidAction("Not Enough Money for These Stocks");
                }
                bankAccount.put(agent, bankAccount.get(transfer.to) + transfer.amount);
                // TODO: unlock bank

                if (transfer.amount > 0) {
                    return transfer.amount + " added to account " + transfer.to + ".";
                }
                else {
                    return (-transfer.amount) + "removed from account" + transfer.to + ".";
                }
            }
            default: {
                return Utils.invalidAction("Action Not Supported");
            }
        }
    }
}
