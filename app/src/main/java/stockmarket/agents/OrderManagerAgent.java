package stockmarket.agents;

import jade.core.Agent;
import stockmarket.behaviours.RequestResponderBehaviour;
import stockmarket.behaviours.managers.protocols.responders.LoanPermissionResponder;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class OrderManagerAgent extends Agent {
    public void setup() {
        // Register
		Utils.registerInYellowPages(this, AgentType.ORDER);

        addBehaviour(new RequestResponderBehaviour(this, new LoanPermissionResponder()));

        Utils.log(this, "Ready");
    }

    public void takedown() {
		// Unregister
        Utils.unregisterFromYellowPages(this);
    }
}
