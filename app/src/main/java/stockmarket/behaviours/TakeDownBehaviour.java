package stockmarket.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import stockmarket.utils.Utils;

public class TakeDownBehaviour extends OneShotBehaviour {
    private Agent agent;

    public TakeDownBehaviour(Agent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        Utils.sleep(1);
        // agent.takedown(); // TODO: takedown agent
    }
}
