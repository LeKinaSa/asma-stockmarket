package stockmarket.behaviours;

import jade.core.behaviours.OneShotBehaviour;
import stockmarket.agents.MyAgent;

public class EndSimulationBehaviour extends OneShotBehaviour {
    private final MyAgent agent;

    public EndSimulationBehaviour(MyAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        agent.finish();
    }
}
