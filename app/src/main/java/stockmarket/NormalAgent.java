package stockmarket;

import java.util.Arrays;
import java.util.List;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.RequestInitiatorBehaviour;
import stockmarket.listeners.messages.NewDayListener;
import stockmarket.listeners.messages.OracleTipListener;
import stockmarket.listeners.protocols.RequestInitiator;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class NormalAgent extends Agent {
	private NewDayListener newDayListener = new NewDayListener();
	private OracleTipListener oracleTipListener = new OracleTipListener();
	private RequestInitiator initiator = new RequestInitiator();
	private boolean readyToChangeDay = true;
	private List<String> bankAgents  = Arrays.asList("bank"); // TODO: fix this magic
	private List<String> stockAgents = Arrays.asList("stockmarket"); // TODO: fix this magic
	private List<String> timeAgents  = Arrays.asList("time"); // TODO: fix this magic

	public void setup() {
		Utils.log(this, "Ready");

		// Initialize Agent, Bank Account and Owned Stocks
		Action startBankAccount = new Action(ActionType.START,  "0");
		Action startOwnStocks   = new Action(ActionType.START, "{}");
		ACLMessage startBankAccountMessage = initiator.getMessage( bankAgents, startBankAccount);
		ACLMessage startOwnStocksMessage   = initiator.getMessage(stockAgents, startOwnStocks  );
		addBehaviour(new RequestInitiatorBehaviour(this, initiator, startBankAccountMessage,  bankAgents.size()));
		addBehaviour(new RequestInitiatorBehaviour(this, initiator,   startOwnStocksMessage, stockAgents.size()));

		// Repetitive Behaviours
		addBehaviour(new MessageListenerBehaviour(this, newDayListener));
		addBehaviour(new MessageListenerBehaviour(this, oracleTipListener));
		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				if (readyToChangeDay) {
					send(Utils.createDayOverMessage(timeAgents, newDayListener.getDay()));
					readyToChangeDay = false;
				}
			}
		});
	}
}
