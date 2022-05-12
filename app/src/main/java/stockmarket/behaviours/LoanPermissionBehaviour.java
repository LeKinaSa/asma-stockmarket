package stockmarket.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.EnvironmentAgent;
import stockmarket.utils.Utils;

public class LoanPermissionBehaviour extends CyclicBehaviour {
    private final EnvironmentAgent agent;

    public LoanPermissionBehaviour(EnvironmentAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        if (agent.getLoanListener().allReceived(agent.getNAgents())) {
            String betterOffer = agent.getLoanListener().getHighestOffer();
            ACLMessage message = Utils.createLoanPermissionMessage(agent.getAgents(), betterOffer);
            agent.addBehaviour(new SendMessageBehaviour(agent, message, null));
        }
    }
}
