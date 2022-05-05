package stockmarket;

import java.util.Arrays;
import java.util.List;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import stockmarket.agents.messages.DayOverListener;
import stockmarket.behaviours.ListeningBehaviour;
import stockmarket.utils.Utils;

public class TimeManager extends Agent {
    private DayOverListener dayOverListener = new DayOverListener();
    private int nAgents = 2;
    private List<String> receivers = Arrays.asList("stockmarket", "oracle", "a1", "a2"); // TODO: fix this magic

    public void setup() {
        Utils.log(this, "Ready");

        addBehaviour(new ListeningBehaviour(this, dayOverListener));
        addBehaviour(new CyclicBehaviour() { // New Day Sender
            @Override
            public void action() {
                if (dayOverListener.canPassToNextDay(nAgents)) {
                    int nextDay = dayOverListener.nextDay();
                    send(Utils.createNewDayMessage(receivers, nextDay));
                }
            }
        });

        // Make sure all the other agents have initialized
        try {
            Thread.sleep(1000);
        }
        catch (InterruptedException e) {
            return;
        }
        send(Utils.createNewDayMessage(receivers, 0));
    }
}
