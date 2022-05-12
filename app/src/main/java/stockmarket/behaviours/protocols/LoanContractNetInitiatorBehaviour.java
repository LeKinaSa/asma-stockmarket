package stockmarket.behaviours.protocols;

import java.util.Vector;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetInitiator;
import stockmarket.agents.NormalAgent;
import stockmarket.behaviours.managers.protocols.Initiator;
import stockmarket.utils.Loan;
import stockmarket.utils.Utils;

public class LoanContractNetInitiatorBehaviour extends ContractNetInitiator {
    private static final Gson gson = new Gson();
    private final NormalAgent agent;
    private int nResponders;

    public LoanContractNetInitiatorBehaviour(NormalAgent agent, Initiator initiator) {
        super(agent, initiator.getMessage(
            FIPANames.InteractionProtocol.FIPA_CONTRACT_NET, ACLMessage.CFP
        ));
        this.agent = agent;
        this.nResponders = initiator.getNResponders();
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

        ACLMessage message, reply;
        double proposal, bestInterest = agent.getBestInterest();
        for (Object object : responses) {
            message = (ACLMessage) object;
            if (message.getPerformative() == ACLMessage.PROPOSE) {
                Loan loan;
                try {
                    loan = gson.fromJson(message.getContent(), Loan.class);
                }
                catch (JsonSyntaxException exception) {
                    loan = new Loan();
                }

                proposal = loan.getProfit();
                if (proposal <= bestInterest) {
                    loan.deny();
                }

                reply = Utils.createReply(message, ACLMessage.ACCEPT_PROPOSAL, gson.toJson(loan));
                acceptances.addElement(reply);
            }
        }
    }
}
