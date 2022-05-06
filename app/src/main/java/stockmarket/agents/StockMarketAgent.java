package stockmarket.agents;

import java.util.Arrays;
import java.util.List;
import jade.core.Agent;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.RequestResponderBehaviour;
import stockmarket.behaviours.managers.messages.NewDayListener;
import stockmarket.behaviours.managers.protocols.StockMarketManager;
import stockmarket.utils.Utils;

public class StockMarketAgent extends Agent {
    private final List<String> bankAgents = Arrays.asList("bank");  // TODO: fix this magic
    private final NewDayListener newDayListener = new NewDayListener();

	public void setup() {
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
