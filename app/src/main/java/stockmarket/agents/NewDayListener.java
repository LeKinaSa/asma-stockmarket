package stockmarket.agents;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.ActionType;

public class NewDayListener implements Listener {
    private MessageTemplate template = MessageTemplate.and(
		MessageTemplate.MatchPerformative(ACLMessage.INFORM),
        MessageTemplate.MatchOntology(ActionType.NEW_DAY.toString())
    );

    @Override
    public void actionOnReceive(ACLMessage message) {
        // TODO
    }

    @Override
    public MessageTemplate getTemplate() {
        return template;
    }
}
