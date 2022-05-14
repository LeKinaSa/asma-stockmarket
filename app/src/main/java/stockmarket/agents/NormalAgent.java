package stockmarket.agents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jade.lang.acl.ACLMessage;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.protocols.LoanContractNetResponderBehaviour;
import stockmarket.behaviours.protocols.RequestResponderBehaviour;
import stockmarket.behaviours.protocols.SubscriptionInitiatorBehaviour;
import stockmarket.managers.messages.GivePermissionListener;
import stockmarket.managers.messages.NewDayListener;
import stockmarket.managers.messages.OracleTipListener;
import stockmarket.managers.protocols.ContractResponder;
import stockmarket.managers.protocols.EndSimulationResponder;
import stockmarket.utils.AgentType;
import stockmarket.utils.Loan;
import stockmarket.utils.MoneyTransfer;
import stockmarket.utils.Utils;

public class NormalAgent extends MyAgent {
	private final Set<String> environmentAgents = new HashSet<>();
	private final Set<String>      normalAgents = new HashSet<>();
	private final OracleTipListener oracleTipListener = new OracleTipListener();
	private final NewDayListener       newDayListener = new NewDayListener(this);
	private final List<MoneyTransfer> loans = Collections.synchronizedList(new ArrayList<>());
	private final List<Double>  bankBalance = new ArrayList<>();
	private Map<String, Double> stockPrices = null;
	private final double initialMoney = 1000; // TODO
	private final double extraInterestAskedInPercentage = 1; // TODO
	private String companyToInvest;
	private double interest;

	public void setup() {
		// Register
		Utils.registerInYellowPages(this, AgentType.NORMAL);

		// Subscriptions
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.ENVIRONMENT, environmentAgents));
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.NORMAL     ,      normalAgents));

		// Repetitive Behaviours
		addBehaviour(new MessageListenerBehaviour         (this, newDayListener));
		addBehaviour(new MessageListenerBehaviour         (this, oracleTipListener));
		addBehaviour(new MessageListenerBehaviour         (this, new GivePermissionListener(this)));
		addBehaviour(new LoanContractNetResponderBehaviour(this, new ContractResponder(this)));
		addBehaviour(new RequestResponderBehaviour        (this, new EndSimulationResponder(this)));

		Utils.log(this, "Ready");
	}

    public void takeDown() {
		// Unregister
        Utils.unregisterFromYellowPages(this);
    }

	public void save() {
		// Save Bank Balance to File
		Utils.saveBankBalance(this, bankBalance);
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

	public void addLoan(ACLMessage message) {
		String agent = message.getSender().getLocalName();
		Loan loan = Utils.getLoanFromJson(message.getContent());
		if (loan == null) {
			Utils.log(this, "Error when Adding a Loan");
			return;
		}
		double amount = loan.getAmount() * (100 + loan.getProfitPercentage()) / 100;
		MoneyTransfer transfer = new MoneyTransfer(agent, amount);
		loans.add(transfer);
	}

	public List<MoneyTransfer> getLoans() {
		return loans;
	}

	public Map<String, Map<String, Double>> getTips() {
		return oracleTipListener.getTips();
	}

	public void removeDayTips() {
		oracleTipListener.removeDayFromTips(getDay());
	}

	public void setStockPrices(Map<String, Double> stockPrices) {
		this.stockPrices = stockPrices;
	}

	public Map<String, Double> getStockPrices() {
		return stockPrices;
	}

	public double getInitialMoney() {
		return initialMoney;
	}

	public void setBankBalance(double bankBalance) {
		this.bankBalance.add(bankBalance);
	}

	public double getCurrentBankBalance() {
		return bankBalance.get(bankBalance.size() - 1);
	}

	public void setInvestments(String companyToInvest, double interest) {
		this.companyToInvest = companyToInvest;
		this.interest = interest;
	}

	public String getInvestmentCompany() {
		return companyToInvest;
	}

	public double getBestInterest() {
		return interest;
	}

	public double getAskedInterest() {
		return interest + extraInterestAskedInPercentage;
	}
}
