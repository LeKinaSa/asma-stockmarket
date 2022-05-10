package stockmarket.agents;

import java.util.HashSet;
import java.util.Set;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.SubscriptionInitiatorBehaviour;
import stockmarket.behaviours.managers.messages.DayOverListener;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class TimeManagerAgent extends Agent {
    private final Set<String> stockAgents  = new HashSet<>();
    private final Set<String> oracleAgents = new HashSet<>();
    private final Set<String> normalAgents = new HashSet<>();
    private final DayOverListener dayOverListener = new DayOverListener();

    public void setup() {
        // Register
        Utils.registerInYellowPages(this, AgentType.TIME);

        // Subscriptions
        addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.STOCK ,  stockAgents));
        addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.ORACLE, oracleAgents));
        addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.NORMAL, normalAgents));

        // Repetitive Behaviours
        addBehaviour(new MessageListenerBehaviour(this, dayOverListener));
        addBehaviour(new CyclicBehaviour() { // New Day Sender
            @Override
            public void action() {
                if (dayOverListener.canPassToNextDay(getNAgents())) {
                    int nextDay = dayOverListener.nextDay();
                    send(Utils.createNewDayMessage(getReceivers(), nextDay));
                }
            }
        });

        // Make sure all the other agents have initialized
        Utils.sleep(1);

        // Start Simulation
        send(Utils.createNewDayMessage(getReceivers(), 0));

        Utils.log(this, "Ready");
    }

    private int getNAgents() {
        return normalAgents.size();
    }

    private Set<String> getReceivers() {
        Set<String> receivers = new HashSet<>();
        receivers.addAll(normalAgents);
        receivers.addAll(stockAgents);
        receivers.addAll(oracleAgents);
        return receivers;
    }
}
