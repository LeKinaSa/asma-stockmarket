package stockmarket.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;

public class TakeDownBehaviour extends OneShotBehaviour {
    private Agent agent;

    public TakeDownBehaviour(Agent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        // TODO: takedown agent
    }
}
