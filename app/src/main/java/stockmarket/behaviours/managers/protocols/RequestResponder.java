package stockmarket.behaviours.managers.protocols;

import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.Action;
import stockmarket.utils.Utils;

public abstract class RequestResponder {
    private static MessageTemplate template = Utils.getMessageTemplate(
        FIPANames.InteractionProtocol.FIPA_REQUEST, ACLMessage.REQUEST, null
    );

    public MessageTemplate getTemplate() {
        return template;
    }

    public boolean checkAction(ACLMessage request) {
        Action action = Action.toAction(request.getOntology(), request.getContent());
        return action != null;
    }

    public abstract String performAction(ACLMessage request);
}
