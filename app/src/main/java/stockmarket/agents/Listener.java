package stockmarket.agents;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public interface Listener {
    MessageTemplate getTemplate();
    void actionOnReceive(ACLMessage message);
    default ACLMessage getReply(ACLMessage message) {
        return null;
    }
}
