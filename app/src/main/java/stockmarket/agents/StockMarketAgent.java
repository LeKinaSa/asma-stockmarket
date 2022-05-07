package stockmarket.agents;

import java.util.ArrayList;
import java.util.List;
import jade.core.Agent;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.RequestResponderBehaviour;
import stockmarket.behaviours.SubscriptionInitiatorBehaviour;
import stockmarket.behaviours.managers.messages.NewDayListener;
import stockmarket.behaviours.managers.protocols.StockMarketManager;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class StockMarketAgent extends Agent {
    private final List<String> bankAgents = new ArrayList<>();
    private final NewDayListener newDayListener = new NewDayListener();

	public void setup() {
        // Subscriptions
        addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.BANK, bankAgents));

        // Repetitive Behaviours
        StockMarketManager responder = new StockMarketManager(this, newDayListener);
        addBehaviour(new RequestResponderBehaviour(this, responder, responder.getTemplate()));
        addBehaviour(new MessageListenerBehaviour(this, newDayListener));

		Utils.log(this, "Ready");
	}

    public List<String> getBankAgents() {
        return bankAgents;
    }
}
