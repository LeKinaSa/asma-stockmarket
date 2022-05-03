package stockmarket.agents;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.ActionType;

public class DayOverListener implements Listener {
    private MessageTemplate template = MessageTemplate.and(
		MessageTemplate.MatchPerformative(ACLMessage.INFORM),
        MessageTemplate.MatchOntology(ActionType.DAY_OVER.toString())
    );
    private int receivedMessages = 0;
    private int day = 0;

    public boolean canPassToNextDay(int numberOfAgents) {
        return numberOfAgents == receivedMessages;
    }

    public int nextDay() {
        receivedMessages = 0;
        return ++day;
    }

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
}
