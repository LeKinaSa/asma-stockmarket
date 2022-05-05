package stockmarket.listeners.messages;

import jade.lang.acl.ACLMessage;
import stockmarket.listeners.Listener;

public interface MessageListener extends Listener {
    default void actionOnReceive(ACLMessage message) {}
    default ACLMessage getReply(ACLMessage message) {
        return null;
    }
}
