package stockmarket.agents;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import stockmarket.behaviours.EnvironmentBehaviour;
import stockmarket.behaviours.LoanPermissionBehaviour;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.protocols.RequestResponderBehaviour;
import stockmarket.behaviours.protocols.SubscriptionInitiatorBehaviour;
import stockmarket.managers.messages.AskPermissionListener;
import stockmarket.managers.messages.DayOverListener;
import stockmarket.managers.protocols.ResponderManager;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class EnvironmentAgent extends MyAgent {
    private final Set<String> agents = new HashSet<>();
    private final ResponderManager           manager = new ResponderManager(this);
    private final DayOverListener        dayListener = new DayOverListener();
    private final AskPermissionListener loanListener = new AskPermissionListener(this);
    private final EnvironmentBehaviour            newDayBehaviour = new EnvironmentBehaviour(this);
    private final LoanPermissionBehaviour loanPermissionBehaviour = new LoanPermissionBehaviour(this);
    private int numberOfNormalAgents = 0;
    private int delay = 0;

    public void setup() {
        Object[] args = getArguments();
        boolean set = false;
        if (args != null && args.length > 0) {
            try {
                numberOfNormalAgents = Integer.parseInt((String) args[0]);
                set = true;
            }
            catch (NumberFormatException ignored) {}
        }
        if (!set) {
            Utils.info(this, "Using Default Number of Normal Agents : " + numberOfNormalAgents);
        }
        set = false;
        if (args != null && args.length > 1) {
            try {
                delay = Integer.parseInt((String) args[1]);
                set = true;
            }
            catch (NumberFormatException ignored) {}
        }
        if (!set) {
            Utils.info(this, "Using Default Delay : " + delay);
        }

        // Register
        Utils.registerInYellowPages(this, AgentType.ENVIRONMENT);

        // Subscriptions
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.NORMAL, agents, false));

        // Set Simulation Delay and Number Of Agents
        newDayBehaviour.setDelay(delay);
        dayListener.setNumberOfAgents(numberOfNormalAgents);

		// Repetitive Behaviours
        addBehaviour(new RequestResponderBehaviour(this, manager));      // Bank and Stock Market
        addBehaviour(new MessageListenerBehaviour (this, dayListener));  // Time
        addBehaviour(new MessageListenerBehaviour (this, loanListener)); // Order
        addBehaviour(newDayBehaviour);
        addBehaviour(loanPermissionBehaviour);

        Utils.info(this, "Ready");
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
