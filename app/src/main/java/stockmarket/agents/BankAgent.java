package stockmarket.agents;

import jade.core.Agent;
import stockmarket.behaviours.RequestResponderBehaviour;
import stockmarket.behaviours.managers.protocols.BankManager;
import stockmarket.utils.Utils;

public class BankAgent extends Agent {
    public void setup() {
        BankManager responder = new BankManager();
        addBehaviour(new RequestResponderBehaviour(this, responder, responder.getTemplate()));

        Utils.log(this, "Ready");
    }
}
