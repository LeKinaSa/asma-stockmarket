package stockmarket.agents;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import jade.core.Agent;
import stockmarket.behaviours.EnvironmentBehaviour;
import stockmarket.behaviours.LoanPermissionBehaviour;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.RequestResponderBehaviour;
import stockmarket.behaviours.SubscriptionInitiatorBehaviour;
import stockmarket.behaviours.managers.messages.DayOverListener;
import stockmarket.behaviours.managers.messages.LoanPermissionListener;
import stockmarket.behaviours.managers.protocols.responders.ResponderManager;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class EnvironmentAgent extends Agent {
    private final Set<String> agents = new HashSet<>();
    private final ResponderManager manager = new ResponderManager(this);
    private final DayOverListener dayListener = new DayOverListener();
    private final EnvironmentBehaviour newDayBehaviour = new EnvironmentBehaviour(this);
    private final LoanPermissionListener loanListener = new LoanPermissionListener();
    private final LoanPermissionBehaviour loanPermissionBehaviour = new LoanPermissionBehaviour(this);

    public void setup() {
        // Register
        Utils.registerInYellowPages(this, AgentType.ENVIRONMENT);

        // Subscriptions
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.NORMAL, agents));

		// Repetitive Behaviours
        addBehaviour(new RequestResponderBehaviour(this, manager));
        addBehaviour(new MessageListenerBehaviour(this, dayListener));
        addBehaviour(new MessageListenerBehaviour(this, loanListener));
        addBehaviour(newDayBehaviour);
        addBehaviour(loanPermissionBehaviour);

        // Make sure all the other agents have initialized
        Utils.sleep(1);

        // Start Simulation
        newDayBehaviour.sendNewDayMessage(0);

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

    public LoanPermissionListener getLoanListener() {
        return loanListener;
    }

    public Map<String, Map<String, Double>> getStockPrices() {
        return manager.getStockPrices();
    }
}
