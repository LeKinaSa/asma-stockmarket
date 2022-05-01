package stockmarket.agents;

import jade.lang.acl.ACLMessage;

public interface RequestResponder {
    public boolean checkAction(ACLMessage request); // TODO: throw NotUnderstoodException ???
    public String performAction(ACLMessage request);
}
