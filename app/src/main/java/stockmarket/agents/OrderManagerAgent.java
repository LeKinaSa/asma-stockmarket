package stockmarket.agents;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import stockmarket.behaviours.RequestResponderBehaviour;
import stockmarket.behaviours.managers.protocols.responders.RequestResponder;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class OrderManagerAgent extends Agent {
    public void setup() {
        // Register
		Utils.registerInYellowPages(this, AgentType.ORDER);

        addBehaviour(new RequestResponderBehaviour(this, new RequestResponder() {
            @Override public boolean checkAction(ACLMessage request) {return false;}
            @Override public String performAction(ACLMessage request) {return null;}
        })); // TODO: add responder

        Utils.log(this, "Ready");
    }

    public void takedown() {
		// Unregister
        Utils.unregisterFromYellowPages(this);
    }
}
