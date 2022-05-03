package stockmarket.agents;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.ActionType;

public class DayOverListener implements Listener {
    private MessageTemplate template = MessageTemplate.and(
		MessageTemplate.MatchPerformative(ACLMessage.INFORM),
        MessageTemplate.MatchOntology(ActionType.DAY_OVER.toString())
    );

    @Override
    public MessageTemplate getTemplate() {
        return template;
    }
}
