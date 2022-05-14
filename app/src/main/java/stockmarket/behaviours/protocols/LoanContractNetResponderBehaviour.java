package stockmarket.behaviours.protocols;

import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.proto.ContractNetResponder;
import stockmarket.agents.NormalAgent;
import stockmarket.managers.protocols.ContractResponder;
import stockmarket.utils.Loan;
import stockmarket.utils.Utils;

public class LoanContractNetResponderBehaviour extends ContractNetResponder {
    private final NormalAgent agent;
    private final ContractResponder responder;

    public LoanContractNetResponderBehaviour(NormalAgent agent, ContractResponder responder) {
        super(agent, responder.getTemplate());
        this.agent = agent;
        this.responder = responder;
    }

    @Override
    protected ACLMessage handleCfp(ACLMessage cfp) throws NotUnderstoodException, RefuseException {
        String sender = cfp.getSender().getLocalName();
        Utils.log(myAgent, "CFP received from " + sender + ". Action is " + cfp.getContent());

        Loan loan = new Loan(agent.getAskedInterest(), agent.getCurrentBankBalance());

        return Utils.createReply(cfp, ACLMessage.PROPOSE, loan.toString());
    }

    @Override
    protected ACLMessage handleAcceptProposal(ACLMessage cfp, ACLMessage propose, ACLMessage accept) throws FailureException {
        responder.performAction(accept);
        return Utils.createReply(accept, ACLMessage.INFORM, accept.getContent());
    }

    @Override
    protected void handleRejectProposal(ACLMessage cfp, ACLMessage propose, ACLMessage reject) {
        Utils.error(myAgent, "Proposal rejected");
    }
}
