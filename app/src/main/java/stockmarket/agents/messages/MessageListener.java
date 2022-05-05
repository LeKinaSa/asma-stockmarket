package stockmarket.agents.messages;

import jade.lang.acl.ACLMessage;
import stockmarket.agents.Listener;

public interface MessageListener extends Listener {
    default void actionOnReceive(ACLMessage message) {}
    default ACLMessage getReply(ACLMessage message) {
        return null;
    }
}
