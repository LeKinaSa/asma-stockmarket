package stockmarket.behaviours.managers.protocols;

import jade.lang.acl.ACLMessage;
import stockmarket.agents.MyAgent;
import stockmarket.behaviours.TakeDownBehaviour;

public class EndSimulationResponder extends RequestResponder {
    private final MyAgent agent;

    public EndSimulationResponder(MyAgent agent) {
        this.agent = agent;
    }

    @Override
    public String performAction(ACLMessage request) {
        agent.addBehaviour(
            new TakeDownBehaviour(agent)
        );
        return null;
    }
}
