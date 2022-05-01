package stockmarket;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.FIPANames;

import stockmarket.agents.RequestResponder;
import stockmarket.behaviors.RequestResponderBehavior;
import stockmarket.utils.Utils;

public class Bank extends Agent {
    public void setup() {
		Utils.log(this, "Ready");

        RequestResponder responder = new RequestResponder() {

            @Override
            public boolean checkAction(ACLMessage request) {
                // TODO Auto-generated method stub
                return true;
            }

            @Override
            public String performAction(ACLMessage request) {
                // TODO Auto-generated method stub
                return "null";
            }
            
        };

        MessageTemplate template = Utils.getMessageTemplate(
            FIPANames.InteractionProtocol.FIPA_REQUEST, ACLMessage.REQUEST
        );

        addBehaviour(new RequestResponderBehavior(this, responder, template));
    }
}
