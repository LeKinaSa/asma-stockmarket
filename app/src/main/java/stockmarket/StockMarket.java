package stockmarket;

import java.util.Arrays;
import java.util.List;
import jade.core.Agent;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.RequestResponderBehaviour;
import stockmarket.listeners.messages.NewDayListener;
import stockmarket.listeners.protocols.StockMarketManager;
import stockmarket.utils.Utils;

public class StockMarket extends Agent {
    private NewDayListener newDayListener = new NewDayListener();
    private List<String> bankAgents = Arrays.asList("bank"); // TODO: fix this magic

	public void setup() {
        StockMarketManager responder = new StockMarketManager(this, newDayListener);
        addBehaviour(new RequestResponderBehaviour(this, responder, responder.getTemplate()));
        addBehaviour(new MessageListenerBehaviour(this, newDayListener));

		Utils.log(this, "Ready");
	}

    public List<String> getBankAgents() {
        return bankAgents;
    }
}
