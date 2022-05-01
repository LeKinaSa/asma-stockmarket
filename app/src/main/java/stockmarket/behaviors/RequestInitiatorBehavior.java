package stockmarket.behaviors;

import java.util.Vector;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;

import stockmarket.agents.RequestInitiator;
import stockmarket.utils.Utils;

public class RequestInitiatorBehavior extends AchieveREInitiator {
    private RequestInitiator initiator;
    private int nResponders;

    public RequestInitiatorBehavior(Agent agent, RequestInitiator initiator, ACLMessage message, int nResponders) {
        super(agent, message);
        this.initiator = initiator;
        this.nResponders = nResponders;
    }

    protected void handleInform(ACLMessage inform) {
        Utils.log(inform.getSender(), "Successfully performed the requested action");
    }

    protected void handleRefuse(ACLMessage refuse) {
        Utils.log(refuse.getSender(), "Refused to perform the requested action");
        --nResponders;
    }

    protected void handleFailure(ACLMessage failure) {
        if (failure.getSender().equals(myAgent.getAMS())) {
            Utils.log(failure.getSender(), "Responder does not exist");
        }
        else {
            Utils.log(failure.getSender(), "Failed to perform the requested action");
        }
    }

    protected void handleAllResultNotifications(Vector notifications) {
        if (notifications.size() < nResponders) {
            // Some responder didn't reply within the specified timeout
            int missingResponses = nResponders - notifications.size();
            System.out.println("Timeout expired; Missing " + missingResponses + " responses");
        }
        Utils.log(myAgent, "All results received");
    }
}