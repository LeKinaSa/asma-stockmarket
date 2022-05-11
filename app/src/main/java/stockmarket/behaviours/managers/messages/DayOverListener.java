package stockmarket.behaviours.managers.messages;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class DayOverListener implements MessageListener {
    private static final MessageTemplate template = Utils.getMessageTemplate(null, ACLMessage.INFORM, ActionType.DAY_OVER);
    private int receivedMessages = 0;
    private int day = 0;

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

    public boolean canPassToNextDay(int numberOfAgents) {
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
