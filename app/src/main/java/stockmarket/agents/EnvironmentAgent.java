package stockmarket.agents;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import jade.core.Agent;
import stockmarket.behaviours.EnvironmentBehaviour;
import stockmarket.behaviours.LoanPermissionBehaviour;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.managers.messages.DayOverListener;
import stockmarket.behaviours.managers.protocols.ResponderManager;
import stockmarket.behaviours.managers.messages.AskPermissionListener;
import stockmarket.behaviours.protocols.RequestResponderBehaviour;
import stockmarket.behaviours.protocols.SubscriptionInitiatorBehaviour;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class EnvironmentAgent extends Agent {
    private final Set<String> agents = new HashSet<>();
    private final ResponderManager           manager = new ResponderManager(this);
    private final DayOverListener        dayListener = new DayOverListener();
    private final AskPermissionListener loanListener = new AskPermissionListener(this);
    private final EnvironmentBehaviour            newDayBehaviour = new EnvironmentBehaviour(this);
    private final LoanPermissionBehaviour loanPermissionBehaviour = new LoanPermissionBehaviour(this);

    public void setup() {
        // Register
        Utils.registerInYellowPages(this, AgentType.ENVIRONMENT);

        // Subscriptions
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.NORMAL, agents));

        Utils.sleep(1);

		// Repetitive Behaviours
        addBehaviour(new RequestResponderBehaviour(this, manager));      // Bank and Stock Market
        addBehaviour(new MessageListenerBehaviour (this, dayListener));  // Time
        addBehaviour(new MessageListenerBehaviour (this, loanListener)); // Order
        addBehaviour(newDayBehaviour);
        addBehaviour(loanPermissionBehaviour);

        Utils.log(this, "Ready");
    }

    public void takedown() {
		// Unregister
        Utils.unregisterFromYellowPages(this);
    }

    public Set<String> getAgents() {
        return agents;
    }

    public int getNAgents() {
        return agents.size();
    }

    public DayOverListener getDayListener() {
        return dayListener;
    }

    public int getDay() {
        return dayListener.getDay();
    }

    public AskPermissionListener getLoanListener() {
        return loanListener;
    }

    public Map<String, Map<String, Double>> getStockPrices() {
        return manager.getStockPrices();
    }

    public boolean simulationIsOver(int day) {
        return manager.getDailyStocks() == null;
    }
}
