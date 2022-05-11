package stockmarket.behaviours;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREResponder;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.FailureException;
import stockmarket.behaviours.managers.protocols.RequestResponder;
import stockmarket.utils.Utils;

public class RequestResponderBehaviour extends AchieveREResponder {
    private final RequestResponder responder;

    public RequestResponderBehaviour(Agent agent, RequestResponder responder) {
        super(agent, responder.getTemplate());
        this.responder = responder;
    }

    @Override
    protected ACLMessage prepareResponse(ACLMessage request) throws NotUnderstoodException, RefuseException {
        String sender = request.getSender().getLocalName();
        Utils.log(myAgent, "REQUEST received from " + sender + ". Action is " + request.getOntology() + " " + request.getContent());
        if (!responder.checkAction(request)) {
            Utils.log(myAgent, "Refused (" + sender + ")");
            throw new RefuseException("check-failed");
        }
        Utils.log(myAgent, "Agree (" + sender + ")");
        return null;
    }

    @Override
    protected ACLMessage prepareResultNotification(ACLMessage request, ACLMessage response) throws FailureException {
        String sender = request.getSender().getLocalName();
        String actionResult = responder.performAction(request);
        if (actionResult == null) {
            Utils.log(myAgent, "Action Failed (" + sender + ")");
            throw new FailureException("unexpected-error");
        }
        Utils.log(myAgent, "Action successfully performed (" + sender + ")");
        ACLMessage reply = Utils.createReply(request, ACLMessage.INFORM, actionResult);
        return reply;
    }
}
