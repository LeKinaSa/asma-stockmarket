package stockmarket.agents;

import java.util.HashSet;
import java.util.Set;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.managers.messages.NormalAgentNewDayListener;
import stockmarket.behaviours.managers.messages.OracleTipListener;
import stockmarket.behaviours.managers.protocols.ContractResponder;
import stockmarket.behaviours.managers.protocols.Initiator;
import stockmarket.behaviours.protocols.LoanContractNetResponderBehaviour;
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
	private final ContractResponder           loanListener = new ContractResponder(this);
	private final NormalAgentNewDayListener newDayListener = new NormalAgentNewDayListener(this, oracleTipListener, loanListener);
	private double bankBalance;
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
		addBehaviour(new RequestInitiatorBehaviour(this, new Initiator(getEnvironmentAgents(), startBankAccount, null)));
		addBehaviour(new RequestInitiatorBehaviour(this, new Initiator(getEnvironmentAgents(), startOwnStocks  , null)));

		// Repetitive Behaviours
		addBehaviour(new MessageListenerBehaviour         (this, newDayListener));
		addBehaviour(new MessageListenerBehaviour         (this, oracleTipListener));
		addBehaviour(new LoanContractNetResponderBehaviour(this, loanListener));

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
		Set<String> otherAgents = new HashSet<>(normalAgents);
		otherAgents.remove(this.getLocalName());
		return otherAgents;
	}

	public int getDay() {
		return newDayListener.getDay();
	}

	public void setBankBalance(double bankBalance) {
		this.bankBalance = bankBalance;
	}

	public double getCurrentBankBalance() {
		return bankBalance;
	}

	public void setInvestments(String companyToInvest, double interest, int returnInvestmentDay) {
		this.companyToInvest = companyToInvest;
		this.interest = interest;
		this.returnInvestmentDay = returnInvestmentDay;
	}

	public String getInvestmentCompany() {
		return companyToInvest;
	}

	public double getBestInterest() {
		return interest;
	}

	public int getInvestmentOverDay() {
		return returnInvestmentDay;
	}

	public double getAskedInterest() {
		return interest + 0.01; // TODO: make the agent ask for more or not?
	}

	public void invest(ACLMessage message) {
		if (message == null) {
			message = Utils.createDayOverMessage(getEnvironmentAgents(), getDay());
		}

		addBehaviour(new RequestInitiatorBehaviour(this, new Initiator(
			getEnvironmentAgents(),
			new Action(ActionType.BUY_STOCK, null), // TODO: introduce stock here
			message
		)));
	}
}
