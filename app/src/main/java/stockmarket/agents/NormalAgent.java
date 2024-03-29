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
	private double initialMoney = 100;
	private double extraInterestAskedInPercentage = 0.5;
	private String companyToInvest;
	private double interest;

    public void setup() {
        Object[] args = getArguments();
        if (args == null || args.length < 2) {
            Utils.info(this, "use: [<extra_interest> [<initial_money> [<log: 0(false)/1(true)>]]]");
        }
        setVariablesFromArguments(args);

        // Register
        Utils.registerInYellowPages(this, AgentType.NORMAL);

        // Subscriptions
        addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.ENVIRONMENT, environmentAgents,  true));
        addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.NORMAL     ,      normalAgents, false));

        // Repetitive Behaviours
        addBehaviour(new MessageListenerBehaviour         (this, newDayListener));
        addBehaviour(new MessageListenerBehaviour         (this, oracleTipListener));
        addBehaviour(new MessageListenerBehaviour         (this, new GivePermissionListener(this)));
        addBehaviour(new LoanContractNetResponderBehaviour(this, new ContractResponder(this)));
        addBehaviour(new RequestResponderBehaviour        (this, new EndSimulationResponder(this)));

        Utils.info(this, "Ready");
    }

    public void takeDown() {
        // Unregister
        Utils.unregisterFromYellowPages(this);
    }

    public void save() {
        // Save Bank Balance to File
        Utils.saveBankBalance(this, bankBalance);
    }

    public void setVariablesFromArguments(Object[] args) {
        extraInterestAskedInPercentage = setDoubleVarFromArgument (args, 0, extraInterestAskedInPercentage , "Extra Interest Asked on Loans");
        initialMoney                   = setDoubleVarFromArgument (args, 1, initialMoney                   , "Initial Bank Balance");
        log                            = setBooleanVarFromArgument(args, 2, log                            , "Logging");
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
            Utils.error(this, "Error when Adding a Loan");
            return;
        }
        if (loan.getAmount() == 0) {
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
