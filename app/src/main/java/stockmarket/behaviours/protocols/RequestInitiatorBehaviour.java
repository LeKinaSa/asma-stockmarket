package stockmarket.behaviours.protocols;

import java.util.Vector;
import jade.core.Agent;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import stockmarket.behaviours.managers.protocols.Initiator;
import stockmarket.utils.Utils;

public class RequestInitiatorBehaviour extends AchieveREInitiator {
    private final Initiator initiator;
    private int nResponders;

    public RequestInitiatorBehaviour(Agent agent, Initiator initiator) {
        super(agent, initiator.getMessage(
            FIPANames.InteractionProtocol.FIPA_REQUEST, ACLMessage.REQUEST
        ));
        this.initiator = initiator;
        this.nResponders = initiator.getNResponders();
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        myAgent.addBehaviour(initiator.getAfter());
        Utils.log(inform.getSender(), "Successfully performed the requested action");
    }

    @Override
    protected void handleRefuse(ACLMessage refuse) {
        Utils.log(refuse.getSender(), "Refused to perform the requested action");
        -- nResponders;
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
    protected void handleAllResultNotifications(Vector notifications) {
        if (notifications.size() < nResponders) {
            // Some responder didn't reply within the specified timeout
            int missingResponses = nResponders - notifications.size();
            Utils.log(myAgent, "Timeout expired, missing " + missingResponses + " responses");
        }

        Utils.log(myAgent, "All results received");
        for (Object notification : notifications) {
            if (notification instanceof ACLMessage) {
                ACLMessage answer = (ACLMessage) notification;
                Utils.log(answer.getSender(), answer.getContent());
            }
        }
    }
}
