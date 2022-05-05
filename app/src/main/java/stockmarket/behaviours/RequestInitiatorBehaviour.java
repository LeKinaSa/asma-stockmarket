package stockmarket.behaviours;

import java.util.Vector;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import stockmarket.agents.RequestInitiator;
import stockmarket.utils.Utils;

public class RequestInitiatorBehaviour extends AchieveREInitiator {
    private RequestInitiator initiator;
    private int nResponders;

    public RequestInitiatorBehaviour(Agent agent, RequestInitiator initiator, ACLMessage message, int nResponders) {
        super(agent, message);
        this.initiator = initiator;
        this.nResponders = nResponders;
    }

    @Override
    protected void handleInform(ACLMessage inform) {
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
