package stockmarket.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

public class SendMessageBehaviour extends OneShotBehaviour {
    private final Agent agent;
    private final ACLMessage message;
    
    public SendMessageBehaviour(Agent agent, ACLMessage message) {
        this.agent = agent;
        this.message = message;
    }

    @Override
    public void action() {
        agent.send(message);
    }
}
