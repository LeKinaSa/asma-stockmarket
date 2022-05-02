package stockmarket.agents;

import jade.lang.acl.ACLMessage;

public interface Listener {
    ACLMessage getReply(ACLMessage message);
}
