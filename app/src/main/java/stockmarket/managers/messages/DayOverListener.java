package stockmarket.managers.messages;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class DayOverListener implements MessageListener {
    private static final MessageTemplate template = Utils.getMessageTemplate(null, ACLMessage.INFORM, ActionType.DAY_OVER);
    private int numberOfAgents = 0;
    private int receivedMessages = 0;
    private int day = -1;

    @Override
    public MessageTemplate getTemplate() {
        return template;
    }

    @Override
    public void actionOnReceive(ACLMessage message) {
        int messageDay;
        try {
            messageDay = Integer.parseInt(message.getContent());
        }
        catch (NumberFormatException e) {
            return;
        }

        if (day == messageDay) {
            ++ receivedMessages;
        }
    }

    public void setNumberOfAgents(int numberOfAgents) {
        this.numberOfAgents = numberOfAgents;
    }

    public boolean canPassToNextDay() {
        return numberOfAgents > 0 && numberOfAgents == receivedMessages;
    }

    public int nextDay() {
        receivedMessages = 0;
        return ++ day;
    }

    public int getDay() {
        return day;
    }
}
