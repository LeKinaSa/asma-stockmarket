package stockmarket.behaviours;

import java.util.Enumeration;
import java.util.Vector;
import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import stockmarket.agents.protocols.ContractInitiator;
import stockmarket.utils.Utils;

public class ContractNetInitiatorBehaviour extends ContractNetInitiator {
    private ContractInitiator initiator;
    private int nResponders;

    public ContractNetInitiatorBehaviour(Agent agent, ContractInitiator initiator, ACLMessage message, int nResponders) {
        super(agent, message);
        this.initiator = initiator;
        this.nResponders = nResponders;
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        Utils.log(inform.getSender(), "Successfully performed the requested action");
    }

    @Override
    protected void handlePropose(ACLMessage propose, Vector v) {
        String sender = propose.getSender().getLocalName();
        Utils.log(myAgent, "Agent " + sender + "proposed " + propose.getContent());
    }

    @Override
    protected void handleRefuse(ACLMessage refuse) {
        String sender = refuse.getSender().getLocalName();
        Utils.log(myAgent, "Agent " + sender + " refused");
    }

    @Override
    protected void handleFailure(ACLMessage failure) {
        if (failure.getSender().equals(myAgent.getAMS())) {
            Utils.log(failure.getSender(), "Responder does not exist");
        }
        else {
            Utils.log(failure.getSender(), "Failed to perform the requested action");
        }
        
        -- nResponders;
    }
    
    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        if (responses.size() < nResponders) {
            // Some responder didn't reply within the specified timeout
            int missingResponses = nResponders - responses.size();
            Utils.log(myAgent, "Timeout expired, missing " + missingResponses + " responses");
        }

        Utils.log(myAgent, "All proposals received");

        // Evaluate proposals.
        int bestProposal = -1, proposal;
        AID bestProposer = null;
        ACLMessage accept = null;
        for (Object object : responses) {
            ACLMessage message = (ACLMessage) object;
            if (message.getPerformative() == ACLMessage.PROPOSE) {
                ACLMessage reply = Utils.createReply(message, ACLMessage.REJECT_PROPOSAL, null);
                acceptances.addElement(reply);
                proposal = -1;
                try {
                    proposal = Integer.parseInt(message.getContent());
                }
                catch (NumberFormatException ignored) {}
                if (proposal > bestProposal) {
                    bestProposal = proposal;
                    bestProposer = message.getSender();
                    accept = reply;
                }
            }
        }

        // Accept the proposal of the best proposer
        if (accept != null) {
            Utils.log(myAgent, "Accepting proposal " + bestProposal + "from responder " + bestProposer.getLocalName());
            accept.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
        }						
    }
}
