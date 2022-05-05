package stockmarket.agents.messages;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class NewDayListener implements Listener {
    private MessageTemplate template = Utils.getMessageTemplate(null, ACLMessage.INFORM, ActionType.NEW_DAY);
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
