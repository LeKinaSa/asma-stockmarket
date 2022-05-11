package stockmarket.behaviours.protocols;

import jade.core.Agent;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetResponder;
import stockmarket.behaviours.managers.protocols.ContractResponder;
import stockmarket.utils.Utils;

public class ContractNetResponderBehaviour extends ContractNetResponder {
    private final ContractResponder responder;

    public ContractNetResponderBehaviour(Agent agent, ContractResponder responder) {
        super(agent, responder.getTemplate());
        this.responder = responder;
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
        String sender = cfp.getSender().getLocalName();
        Utils.log(myAgent, "CFP received from " + sender + ". Action is " + cfp.getContent());

        int proposal = responder.evaluateAction();

        if (proposal > 2) { // TODO: check this magic 2
            Utils.log(myAgent, "Proposing " + proposal); // Provide Proposal
            return Utils.createReply(cfp, ACLMessage.PROPOSE, String.valueOf(proposal));
        }
        else {
            Utils.log(myAgent, "Refuse"); // Refuse to Provide Proposal
            throw new RefuseException("evaluation-failed");
        }
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
        Utils.log(myAgent, "Proposal accepted");
        String sender = cfp.getSender().getLocalName();

        boolean actionResult = responder.performAction();
        if (!actionResult) {
            Utils.log(myAgent, "Action execution failed");
            throw new FailureException("unexpected-error");
        }
        Utils.log(myAgent, "Action successfully performed (" + sender + ")");
        return Utils.createReply(accept, ACLMessage.INFORM, null);
    }

    @Override
    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        Utils.log(myAgent, "Proposal rejected");
    }
}
