package stockmarket.behaviours.managers.protocols.responders;

import jade.lang.acl.ACLMessage;

public class LoanPermissionResponder extends RequestResponder {
    @Override
    public String performAction(ACLMessage request) {
        // TODO: wait all the answers
        // TODO: compare all the requests
        // TODO: answer all the requests with the name of the agent with the highest interest
        return null;
    }
}
