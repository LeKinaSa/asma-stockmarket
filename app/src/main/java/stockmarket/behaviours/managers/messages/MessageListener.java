package stockmarket.behaviours.managers.messages;

import jade.lang.acl.ACLMessage;
import stockmarket.behaviours.managers.Listener;

public interface MessageListener extends Listener {
    default void actionOnReceive(ACLMessage message) {}
}
