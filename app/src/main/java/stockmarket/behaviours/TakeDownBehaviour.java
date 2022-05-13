package stockmarket.behaviours;

import jade.core.behaviours.OneShotBehaviour;
import stockmarket.agents.MyAgent;
import stockmarket.utils.Utils;

public class TakeDownBehaviour extends OneShotBehaviour {
    private MyAgent agent;

    public TakeDownBehaviour(MyAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        Utils.sleep(1);
        agent.takedown();
    }
}
