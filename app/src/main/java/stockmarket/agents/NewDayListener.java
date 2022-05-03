package stockmarket.agents;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.ActionType;

public class NewDayListener implements Listener {
    private MessageTemplate template = MessageTemplate.and(
		MessageTemplate.MatchPerformative(ACLMessage.INFORM),
        MessageTemplate.MatchOntology(ActionType.NEW_DAY.toString())
    );
    private int day;

    public int getDay() {
        return day;
    }

    @Override
    public MessageTemplate getTemplate() {
        return template;
    }

    @Override
    public void actionOnReceive(ACLMessage message) {
        try {
            day = Integer.parseInt(message.getContent());
        }
        catch (NumberFormatException ignored) {}
    }
}
