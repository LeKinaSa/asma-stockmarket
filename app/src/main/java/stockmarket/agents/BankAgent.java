package stockmarket.agents;

import java.util.ArrayList;
import java.util.List;
import jade.core.Agent;
import stockmarket.behaviours.RequestResponderBehaviour;
import stockmarket.behaviours.SubscriptionInitiatorBehaviour;
import stockmarket.behaviours.managers.protocols.BankManager;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class BankAgent extends Agent {
    private final List<String> stockAgents = new ArrayList<>();

    public void setup() {
        // Subscriptions
        addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.STOCK, stockAgents));

        BankManager responder = new BankManager(this);
        addBehaviour(new RequestResponderBehaviour(this, responder, responder.getTemplate()));

        Utils.log(this, "Ready");
    }

    public boolean knowsStockAgent(String agent) {
        return stockAgents.contains(agent);
    }
}
