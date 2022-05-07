package stockmarket.agents;

import java.util.ArrayList;
import java.util.List;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.RequestInitiatorBehaviour;
import stockmarket.behaviours.SubscriptionInitiatorBehaviour;
import stockmarket.behaviours.managers.messages.NewDayListener;
import stockmarket.behaviours.managers.messages.OracleTipListener;
import stockmarket.behaviours.managers.protocols.RequestInitiator;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class NormalAgent extends Agent {
	private final List<String>   bankAgents = new ArrayList<>();
	private final List<String>  stockAgents = new ArrayList<>();
	private final List<String>   timeAgents = new ArrayList<>();
	private final List<String> normalAgents = new ArrayList<>();
	private NewDayListener newDayListener = new NewDayListener();
	private OracleTipListener oracleTipListener = new OracleTipListener();
	private boolean readyToChangeDay = true;

	public void setup() {
		// Subscriptions
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.BANK  ,   bankAgents));
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.STOCK ,  stockAgents));
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.TIME  ,   timeAgents));
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.NORMAL, normalAgents));

		// Initialize Agent, Bank Account and Owned Stocks
		Action startBankAccount = new Action(ActionType.START,  "0");
		Action startOwnStocks   = new Action(ActionType.START, "{}");
		addBehaviour(new RequestInitiatorBehaviour(this, new RequestInitiator( getBankAgents(), startBankAccount)));
		addBehaviour(new RequestInitiatorBehaviour(this, new RequestInitiator(getStockAgents(), startOwnStocks  )));

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

	public List<String> getBankAgents() {
		return bankAgents;
	}

	public List<String> getStockAgents() {
		return stockAgents;
	}

	public List<String> getTimeAgents() {
		return timeAgents;
	}
}
