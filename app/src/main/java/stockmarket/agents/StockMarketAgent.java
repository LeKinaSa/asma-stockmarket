package stockmarket.agents;

import java.util.HashSet;
import java.util.Set;
import jade.core.Agent;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.RequestResponderBehaviour;
import stockmarket.behaviours.SubscriptionInitiatorBehaviour;
import stockmarket.behaviours.managers.messages.NewDayListener;
import stockmarket.behaviours.managers.protocols.responders.StockMarketManager;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class StockMarketAgent extends Agent {
    private final Set<String> bankAgents = new HashSet<>();
    private final NewDayListener newDayListener = new NewDayListener();

	public void setup() {
        // Register
        Utils.registerInYellowPages(this, AgentType.STOCK);

        // Subscriptions
        addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.BANK, bankAgents));

        // Repetitive Behaviours
        addBehaviour(new RequestResponderBehaviour(this, new StockMarketManager(this, newDayListener)));
        addBehaviour(new MessageListenerBehaviour(this, newDayListener));

		Utils.log(this, "Ready");
	}

    public void takedown() {
		// Unregister
        Utils.unregisterFromYellowPages(this);
    }

    public Set<String> getBankAgents() {
        return bankAgents;
    }
}
