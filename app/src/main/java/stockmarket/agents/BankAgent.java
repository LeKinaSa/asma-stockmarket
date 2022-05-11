package stockmarket.agents;

import java.util.HashSet;
import java.util.Set;
import jade.core.Agent;
import stockmarket.behaviours.RequestResponderBehaviour;
import stockmarket.behaviours.SubscriptionInitiatorBehaviour;
import stockmarket.behaviours.managers.protocols.responders.BankManager;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class BankAgent extends Agent {
    private final Set<String> stockAgents = new HashSet<>();

    public void setup() {
        // Register
        Utils.registerInYellowPages(this, AgentType.BANK);

        // Subscriptions
        addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.STOCK, stockAgents));

        // Repetitive Behaviours
        addBehaviour(new RequestResponderBehaviour(this, new BankManager(this)));

        Utils.log(this, "Ready");
    }

    public boolean knowsStockAgent(String agent) {
        return stockAgents.contains(agent);
    }

    public void takedown() {
		// Unregister
        Utils.unregisterFromYellowPages(this);
    }
}
