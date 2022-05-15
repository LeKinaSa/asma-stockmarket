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
    private int simulationDays       = 1260;
    private int agentsToTip          = 1;
    private int tipDays              = 2;
    private int numberOfTipsPerDay   = 10;
    private int delay                = 0;

    public void setup() {
        Object[] args = getArguments();
        if (args == null || args.length != 6) {
            Utils.info(this, "use: <n_normal_agents> [<simulation_days> [<n_agents_to_tip> [<n_days_to_tip> [<n_tips_per_day> [<delay>]]]]]");
        }
        setVariablesFromArguments(getArguments());
        if (numberOfNormalAgents < 2) {
            Utils.error(this, "Number of Agents must be at least 2");
        }

        // Register
        Utils.registerInYellowPages(this, AgentType.ENVIRONMENT);

        // Subscriptions
        addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.NORMAL, agents, false));

        // Set Simulation Delay and Number Of Agents
        newDayBehaviour.setDelay(delay);
        newDayBehaviour.setAgentsToTip(agentsToTip);
        newDayBehaviour.setTipDays(tipDays);
        newDayBehaviour.setNumberOfTipsPerDay(numberOfTipsPerDay);
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

    public void setVariablesFromArguments(Object[] args) {
        numberOfNormalAgents = setIntegerVarFromArgument(args, 0, numberOfNormalAgents, "Number of Normal Agents");
        simulationDays       = setIntegerVarFromArgument(args, 1, simulationDays      , "Number of Days in Simulation");
        agentsToTip          = setIntegerVarFromArgument(args, 2, agentsToTip         , "Number of Agents to Tip");
        tipDays              = setIntegerVarFromArgument(args, 3, tipDays             , "Number of Days to Tip");
        numberOfTipsPerDay   = setIntegerVarFromArgument(args, 4, numberOfTipsPerDay  , "Number of Tips per Day");
        delay                = setIntegerVarFromArgument(args, 5, delay               , "Delay");
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

    public int getSimulationDays() {
        return simulationDays;
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
