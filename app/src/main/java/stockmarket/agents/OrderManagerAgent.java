package stockmarket.agents;

import jade.core.Agent;
import stockmarket.behaviours.RequestResponderBehaviour;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class OrderManagerAgent extends Agent {
    public void setup() {
        // Register
		Utils.registerInYellowPages(this, AgentType.ORDER);

        addBehaviour(new RequestResponderBehaviour(this, null, null)); // TODO: add responder

        Utils.log(this, "Ready");
    }

    public void takedown() {
		// Unregister
        Utils.unregisterFromYellowPages(this);
    }
}
