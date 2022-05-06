package stockmarket.behaviours;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import stockmarket.behaviours.managers.messages.MessageListener;
import stockmarket.utils.Utils;

public class MessageListenerBehaviour extends CyclicBehaviour {
    private final MessageListener listener;

    public MessageListenerBehaviour(Agent agent, MessageListener listener) {
        super(agent);
        this.listener = listener;
    }

    @Override
    public void action() {
        ACLMessage message = myAgent.receive(listener.getTemplate());
        if (message != null) {
            String sender = message.getSender().getLocalName();
            Utils.log(myAgent, "Received from " + sender + ". Action is " + message.getOntology() + " " + message.getContent());
            listener.actionOnReceive(message);
            ACLMessage reply = listener.getReply(message);
            if (reply != null) {
                myAgent.send(reply);
            }
        }
        else {
            block();
        }
    }
}
