package stockmarket.agents;

import java.util.Arrays;
import java.util.List;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.RequestInitiatorBehaviour;
import stockmarket.behaviours.managers.messages.NewDayListener;
import stockmarket.behaviours.managers.messages.OracleTipListener;
import stockmarket.behaviours.managers.protocols.RequestInitiator;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class NormalAgent extends Agent {
	private NewDayListener newDayListener = new NewDayListener();
	private OracleTipListener oracleTipListener = new OracleTipListener();
	private boolean readyToChangeDay = true;
	private List<String> bankAgents  = Arrays.asList("bank"); // TODO: fix this magic
	private List<String> stockAgents = Arrays.asList("stockmarket"); // TODO: fix this magic
	private List<String> timeAgents  = Arrays.asList("time"); // TODO: fix this magic

	public void setup() {
		// Initialize Agent, Bank Account and Owned Stocks
		Action startBankAccount = new Action(ActionType.START,  "0");
		Action startOwnStocks   = new Action(ActionType.START, "{}");
		addBehaviour(new RequestInitiatorBehaviour(this, new RequestInitiator( bankAgents, startBankAccount)));
		addBehaviour(new RequestInitiatorBehaviour(this, new RequestInitiator(stockAgents, startOwnStocks  )));

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

		Utils.log(this, "Ready");
	}
}
