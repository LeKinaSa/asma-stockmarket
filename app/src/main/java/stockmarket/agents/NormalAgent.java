package stockmarket.agents;

import java.util.HashSet;
import java.util.Set;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import stockmarket.behaviours.ContractNetResponderBehaviour;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.RequestInitiatorBehaviour;
import stockmarket.behaviours.SubscriptionInitiatorBehaviour;
import stockmarket.behaviours.managers.messages.NormalAgentNewDayListener;
import stockmarket.behaviours.managers.messages.OracleTipListener;
import stockmarket.behaviours.managers.protocols.initiators.RequestInitiator;
import stockmarket.behaviours.managers.protocols.responders.ContractResponder;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class NormalAgent extends Agent {
	private final Set<String>   bankAgents = new HashSet<>();
	private final Set<String>  stockAgents = new HashSet<>();
	private final Set<String>   timeAgents = new HashSet<>();
	private final Set<String>  orderAgents = new HashSet<>();
	private final Set<String> normalAgents = new HashSet<>();
	private final OracleTipListener oracleTipListener = new OracleTipListener();
	private final ContractResponder loanListener = new ContractResponder();
	private final NormalAgentNewDayListener newDayListener = new NormalAgentNewDayListener(this, oracleTipListener, loanListener);
	private boolean readyToChangeDay = true;

	public void setup() {
		// Register
		Utils.registerInYellowPages(this, AgentType.NORMAL);

		// Subscriptions
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.BANK  ,   bankAgents));
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.STOCK ,  stockAgents));
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.TIME  ,   timeAgents));
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.ORDER,   orderAgents));
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.NORMAL, normalAgents));

		// Initialize Agent, Bank Account and Owned Stocks
		Action startBankAccount = new Action(ActionType.START_BANK,  "0");
		Action startOwnStocks   = new Action(ActionType.START_STOCK, "{}");
		addBehaviour(new RequestInitiatorBehaviour(this, new RequestInitiator( getBankAgents(), startBankAccount)));
		addBehaviour(new RequestInitiatorBehaviour(this, new RequestInitiator(getStockAgents(), startOwnStocks  )));

		// Repetitive Behaviours
		addBehaviour(new MessageListenerBehaviour(this, newDayListener));
		addBehaviour(new MessageListenerBehaviour(this, oracleTipListener));
		addBehaviour(new ContractNetResponderBehaviour(this, loanListener));
		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				if (readyToChangeDay && timeAgents.size() > 0) {
					send(Utils.createDayOverMessage(timeAgents, newDayListener.getDay()));
					readyToChangeDay = false;
				}
			}
		});

		Utils.log(this, "Ready");
	}

    public void takedown() {
		// Unregister
        Utils.unregisterFromYellowPages(this);
    }

	public Set<String> getBankAgents() {
		return bankAgents;
	}

	public Set<String> getStockAgents() {
		return stockAgents;
	}

	public Set<String> getTimeAgents() {
		return timeAgents;
	}

	public Set<String> getOrderAgents() {
		return orderAgents;
	}

	public Set<String> getNormalAgents() {
		return normalAgents;
	}
}
