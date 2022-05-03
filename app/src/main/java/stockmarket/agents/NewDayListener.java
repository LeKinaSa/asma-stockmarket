package stockmarket.agents;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.ActionType;

public class NewDayListener implements Listener {
    private MessageTemplate template = MessageTemplate.and(
		MessageTemplate.MatchPerformative(ACLMessage.INFORM),
        MessageTemplate.MatchContent(ActionType.NEW_DAY.toString()) // TODO: check partial match
    );

    @Override
    public MessageTemplate getTemplate() {
        return template;
    }

    @Override
    public ACLMessage getReply(ACLMessage message) {
        return null;
    }
}
