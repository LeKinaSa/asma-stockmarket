package stockmarket.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import stockmarket.agents.EnvironmentAgent;

public class LoanPermissionBehaviour extends CyclicBehaviour {
    private final EnvironmentAgent agent;

    public LoanPermissionBehaviour(EnvironmentAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        if (agent.getLoanListener().allReceived(agent.getNAgents())) {
            String betterOffer = agent.getLoanListener().getHighestOffer();
            sendLoanPermissionMessage(betterOffer);
            agent.getLoanListener().restart();
        }
    }

    public void sendLoanPermissionMessage(String betterOffer) {
        // TODO
    }
}
