package stockmarket.behaviours.managers.protocols;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import stockmarket.behaviours.TakeDownBehaviour;

public class EndSimulationResponder extends RequestResponder {
    private final Agent agent;

    public EndSimulationResponder(Agent agent) {
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
