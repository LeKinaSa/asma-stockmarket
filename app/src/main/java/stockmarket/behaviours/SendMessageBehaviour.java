package stockmarket.behaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import stockmarket.behaviours.managers.protocols.Initiator;

public class SendMessageBehaviour extends OneShotBehaviour {
    private final Agent      agent;
    private final ACLMessage message;
    private final Initiator  initiator;
    
    public SendMessageBehaviour(Agent agent, ACLMessage message, Initiator initiator) {
        this.agent     = agent;
        this.message   = message;
        this.initiator = initiator;
    }

    @Override
    public void action() {
        agent.send(message);
        agent.addBehaviour(initiator.getAfter());
    }
}
