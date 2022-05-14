package stockmarket.behaviours.protocols;

import java.util.Vector;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import stockmarket.agents.NormalAgent;
import stockmarket.managers.protocols.Initiator;
import stockmarket.utils.Loan;
import stockmarket.utils.Utils;

public class LoanContractNetInitiatorBehaviour extends ContractNetInitiator {
    private final NormalAgent agent;
    private final Initiator initiator;
    private int nResponders;

    public LoanContractNetInitiatorBehaviour(NormalAgent agent, Initiator initiator) {
        super(agent, initiator.getMessage(
            FIPANames.InteractionProtocol.FIPA_CONTRACT_NET, ACLMessage.CFP
        ));
        this.agent = agent;
        this.initiator = initiator;
        this.nResponders = initiator.getNResponders();
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        agent.addLoan(inform);
        -- nResponders;
        if (nResponders == 0) {
            Utils.log(inform.getSender(), "Successfully performed the requested action");
            double interest = Math.round(agent.getBestInterest() * 100) / 100;
            Utils.info(agent, "Investing at " + interest + "%");
            initiator.activateNextBehaviour(agent);
        }
    }

    @Override
    protected void handlePropose(ACLMessage propose, Vector v) {
        String sender = propose.getSender().getLocalName();
        Utils.log(myAgent, "Agent " + sender + " proposed " + propose.getContent());
    }

    @Override
    protected void handleRefuse(ACLMessage refuse) {
        String sender = refuse.getSender().getLocalName();
        Utils.error(myAgent, "Agent " + sender + " refused");
    }

    @Override
    protected void handleFailure(ACLMessage failure) {
        if (failure.getSender().equals(myAgent.getAMS())) {
            Utils.error(failure.getSender(), "Responder does not exist");
        }
        else {
            Utils.error(failure.getSender(), "Failed to perform the requested action");
        }

        -- nResponders;
    }

    @Override
    protected void handleAllResponses(Vector responses, Vector acceptances) {
        if (responses.size() < nResponders) {
            // Some responder didn't reply within the specified timeout
            int missingResponses = nResponders - responses.size();
            Utils.error(myAgent, "Timeout expired, missing " + missingResponses + " responses");
        }

        Utils.log(myAgent, "All proposals received");

        ACLMessage message, reply;
        double proposal, bestInterest = agent.getBestInterest();
        for (Object object : responses) {
            message = (ACLMessage) object;
            if (message.getPerformative() == ACLMessage.PROPOSE) {
                Loan loan = Utils.getLoanFromJson(message.getContent());
                if (loan == null) {
                    loan = new Loan();
                }

                proposal = loan.getProfitPercentage();
                if (proposal >= bestInterest) {
                    loan.deny();
                }

                reply = Utils.createReply(message, ACLMessage.ACCEPT_PROPOSAL, loan.toString());
                acceptances.addElement(reply);
            }
        }
    }
}
