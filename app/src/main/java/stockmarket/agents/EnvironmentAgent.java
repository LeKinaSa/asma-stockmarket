package stockmarket.agents;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import jade.core.behaviours.Behaviour;
import stockmarket.behaviours.EnvironmentBehaviour;
import stockmarket.behaviours.LoanPermissionBehaviour;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.StartAgent;
import stockmarket.behaviours.managers.messages.DayOverListener;
import stockmarket.behaviours.managers.protocols.ResponderManager;
import stockmarket.behaviours.managers.messages.AskPermissionListener;
import stockmarket.behaviours.protocols.RequestResponderBehaviour;
import stockmarket.behaviours.protocols.SubscriptionInitiatorBehaviour;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class EnvironmentAgent extends MyAgent {
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

        // Initialize Agent
        Queue<Behaviour> queuedBehaviours = new LinkedList<>();

		// Repetitive Behaviours
        queuedBehaviours.add(new RequestResponderBehaviour(this, manager));      // Bank and Stock Market
        queuedBehaviours.add(new MessageListenerBehaviour (this, dayListener));  // Time
        queuedBehaviours.add(new MessageListenerBehaviour (this, loanListener)); // Order
        queuedBehaviours.add(newDayBehaviour);
        queuedBehaviours.add(loanPermissionBehaviour);

        // Start Agent
        addBehaviour(new StartAgent(this, queuedBehaviours));
        Utils.log(this, "Ready");
    }

    public void takeDown() {
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
