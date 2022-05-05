package stockmarket.agents.messages;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public interface Listener {
    MessageTemplate getTemplate();
    default void actionOnReceive(ACLMessage message) {}
    default ACLMessage getReply(ACLMessage message) {
        return null;
    }
}
