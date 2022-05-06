package stockmarket.agents;

import java.util.Arrays;
import java.util.List;
import jade.core.Agent;
import stockmarket.behaviours.RequestResponderBehaviour;
import stockmarket.behaviours.managers.protocols.BankManager;
import stockmarket.utils.Utils;

public class BankAgent extends Agent {
    private final List<String> stockAgents = Arrays.asList("stockmarket"); // TODO: fix this magic

    public void setup() {
        BankManager responder = new BankManager(this);
        addBehaviour(new RequestResponderBehaviour(this, responder, responder.getTemplate()));

        Utils.log(this, "Ready");
    }

    public boolean knowsStockAgent(String agent) {
        return stockAgents.contains(agent);
    }
}
