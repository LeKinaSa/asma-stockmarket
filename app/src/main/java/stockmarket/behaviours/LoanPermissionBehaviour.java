package stockmarket.behaviours;

import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.EnvironmentAgent;
import stockmarket.behaviours.managers.protocols.Initiator;
import stockmarket.utils.Utils;

public class LoanPermissionBehaviour extends CyclicBehaviour {
    private final EnvironmentAgent agent;

    public LoanPermissionBehaviour(EnvironmentAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        if (agent.getLoanListener().allReceived(agent.getNAgents())) {
            String bestOffer = agent.getLoanListener().getHighestOffer();
            ACLMessage message = Utils.createLoanPermissionMessage(agent.getAgents(), bestOffer);
            agent.addBehaviour(new SendMessageBehaviour(agent, message, new Initiator(null)));
        }
    }
}
