package stockmarket;

import jade.core.Agent;
import stockmarket.behaviours.RequestResponderBehaviour;
import stockmarket.listeners.protocols.BankManager;
import stockmarket.utils.Utils;

public class Bank extends Agent {
    public void setup() {
        BankManager responder = new BankManager();
        addBehaviour(new RequestResponderBehaviour(this, responder, responder.getTemplate()));

        Utils.log(this, "Ready");
    }
}
