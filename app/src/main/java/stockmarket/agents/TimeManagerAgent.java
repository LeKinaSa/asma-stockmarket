package stockmarket.agents;

import java.util.ArrayList;
import java.util.List;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.SubscriptionInitiatorBehaviour;
import stockmarket.behaviours.managers.messages.DayOverListener;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class TimeManagerAgent extends Agent {
    private final List<String> stockAgents  = new ArrayList<>();
    private final List<String> oracleAgents = new ArrayList<>();
    private final List<String> normalAgents = new ArrayList<>();
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

    private List<String> getReceivers() {
        List<String> receivers = new ArrayList<>();
        receivers.addAll(normalAgents);
        receivers.addAll(stockAgents);
        receivers.addAll(oracleAgents);
        return receivers;
    }
}
