package stockmarket;

import jade.core.Agent;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.RequestResponderBehaviour;
import stockmarket.listeners.messages.NewDayListener;
import stockmarket.listeners.protocols.StockMarketManager;
import stockmarket.utils.Utils;

public class StockMarket extends Agent {
    private NewDayListener newDayListener = new NewDayListener();

	public void setup() {
		Utils.log(this, "Ready");

        StockMarketManager responder = new StockMarketManager();
        addBehaviour(new RequestResponderBehaviour(this, responder, responder.getTemplate()));
        addBehaviour(new MessageListenerBehaviour(this, newDayListener));
	}
}
