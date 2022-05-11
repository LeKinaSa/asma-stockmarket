package stockmarket.behaviours.managers.protocols.responders;

import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.Utils;

public abstract class RequestResponder {
    private static MessageTemplate template = Utils.getMessageTemplate(
        FIPANames.InteractionProtocol.FIPA_REQUEST, ACLMessage.REQUEST, null
    );

    public MessageTemplate getTemplate() {
        return template;
    }

    public abstract boolean checkAction(ACLMessage request);

    public abstract String performAction(ACLMessage request);
}
