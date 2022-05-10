package stockmarket.behaviours.managers.protocols;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.BankAgent;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.MoneyTransfer;
import stockmarket.utils.Utils;

public class BankManager extends RequestResponder {
    private final static Gson gson = new Gson();
    private final Map<String, Double> bankAccount = new HashMap<>();
    private final BankAgent bank;

    public BankManager(BankAgent bank) {
        this.bank = bank;
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
                double value = 0D;
                try {
                    value = Double.parseDouble(action.getInformation());
                }
                catch (NumberFormatException ignored) {}

                synchronized (bankAccount) {
                    if (!bankAccount.containsKey(agent)) {
                        bankAccount.put(agent, value);
                        return "Started Bank Account with " + value + ".";
                    }
                }
            }
            case CHECK_BALANCE: {
                double balance;
                synchronized (bankAccount) {
                    balance = bankAccount.get(agent);
                }
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

                double balance;
                synchronized (bankAccount) {
                    if (!bankAccount.containsKey(transfer.to)) {
                        return Utils.invalidAction("Unknown Transfer Destination Agent");
                    }
                    if (bankAccount.get(agent) < transfer.amount) {
                        return Utils.invalidAction("Not Enough Money for Transfer");
                    }
                    bankAccount.put(agent, bankAccount.get(agent)       - transfer.amount);
                    bankAccount.put(agent, bankAccount.get(transfer.to) + transfer.amount);

                    balance = bankAccount.get(agent);
                }

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

                if (!bank.knowsStockAgent(agent)) {
                    return Utils.invalidAction("Only the Stock Market can Perform this Action");
                }

                synchronized (bankAccount) {
                    if (!bankAccount.containsKey(transfer.to)) {
                        return Utils.invalidAction("Unknown Agent is Managing the Stocks");
                    }
                    if (transfer.amount < 0 && -transfer.amount > bankAccount.get(transfer.to)) {
                        return Utils.invalidAction("Not Enough Money for These Stocks");
                    }
                    bankAccount.put(agent, bankAccount.get(transfer.to) + transfer.amount);
                }

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
