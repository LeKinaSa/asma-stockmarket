package stockmarket.behaviours;

import java.util.Queue;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import stockmarket.utils.Utils;

public class StartAgent extends OneShotBehaviour {
    private static final int waitSeconds = 1;
    private final Agent agent;
    private final Queue<Behaviour> queuedBehaviours;

    public StartAgent(Agent agent, Queue<Behaviour> queuedBehaviours) {
        this.agent = agent;
        this.queuedBehaviours = queuedBehaviours;
    }

    @Override
    public void action() {
        Utils.sleep(waitSeconds);

        while (!queuedBehaviours.isEmpty()) {
            agent.addBehaviour(queuedBehaviours.remove());
        }
    }
}
