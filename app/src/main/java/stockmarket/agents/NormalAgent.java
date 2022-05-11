package stockmarket.agents;

import java.util.HashSet;
import java.util.Set;
import jade.core.Agent;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.managers.messages.NormalAgentNewDayListener;
import stockmarket.behaviours.managers.messages.OracleTipListener;
import stockmarket.behaviours.managers.protocols.initiators.RequestInitiator;
import stockmarket.behaviours.managers.protocols.responders.ContractResponder;
import stockmarket.behaviours.protocols.ContractNetResponderBehaviour;
import stockmarket.behaviours.protocols.RequestInitiatorBehaviour;
import stockmarket.behaviours.protocols.SubscriptionInitiatorBehaviour;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class NormalAgent extends Agent {
	private final Set<String> environmentAgents = new HashSet<>();
	private final Set<String>      normalAgents = new HashSet<>();
	private final OracleTipListener      oracleTipListener = new OracleTipListener();
	private final ContractResponder           loanListener = new ContractResponder();
	private final NormalAgentNewDayListener newDayListener = new NormalAgentNewDayListener(this, oracleTipListener, loanListener);
	private String companyToInvest;
	private double interest;
	private int    returnInvestmentDay;

	public void setup() {
		// Register
		Utils.registerInYellowPages(this, AgentType.NORMAL);

		// Subscriptions
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.ENVIRONMENT, environmentAgents));
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.NORMAL     ,      normalAgents));

		// Initialize Agent, Bank Account and Owned Stocks
		Action startBankAccount = new Action(ActionType.START_BANK,  "0");
		Action startOwnStocks   = new Action(ActionType.START_STOCK, "{}");
		addBehaviour(new RequestInitiatorBehaviour(this, new RequestInitiator(getEnvironmentAgents(), startBankAccount)));
		addBehaviour(new RequestInitiatorBehaviour(this, new RequestInitiator(getEnvironmentAgents(), startOwnStocks  )));

		// Repetitive Behaviours
		addBehaviour(new MessageListenerBehaviour     (this, newDayListener));
		addBehaviour(new MessageListenerBehaviour     (this, oracleTipListener));
		addBehaviour(new ContractNetResponderBehaviour(this, loanListener));

		Utils.log(this, "Ready");
	}

    public void takedown() {
		// Unregister
        Utils.unregisterFromYellowPages(this);
    }

	public Set<String> getEnvironmentAgents() {
		return environmentAgents;
	}

	public Set<String> getNormalAgents() {
		return normalAgents;
	}

	public void setInvestments(String companyToInvest, double interest, int returnInvestmentDay) {
		this.companyToInvest = companyToInvest;
		this.interest = interest;
		this.returnInvestmentDay = returnInvestmentDay;
	} // TODO: use gets too
}
