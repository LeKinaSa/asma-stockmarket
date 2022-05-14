package stockmarket.managers.protocols;

import jade.lang.acl.ACLMessage;
import stockmarket.agents.MyAgent;
import stockmarket.behaviours.EndSimulationBehaviour;
import stockmarket.utils.ActionType;

public class EndSimulationResponder extends RequestResponder {
    private final MyAgent agent;

    public EndSimulationResponder(MyAgent agent) {
        this.agent = agent;
    }

    @Override
    public String performAction(ACLMessage request) {
        agent.addBehaviour(
            new EndSimulationBehaviour(agent)
        );
        return ActionType.END_SIMULATION.toString();
    }
}
