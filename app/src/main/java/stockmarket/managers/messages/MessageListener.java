package stockmarket.managers.messages;

import jade.lang.acl.ACLMessage;
import stockmarket.managers.Listener;

public interface MessageListener extends Listener {
    default void actionOnReceive(ACLMessage message) {}
}
