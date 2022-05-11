package stockmarket.behaviours.managers.messages;

import java.util.HashMap;
import java.util.Map;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class LoanPermissionListener implements MessageListener {
    private static final MessageTemplate template = Utils.getMessageTemplate(null, ACLMessage.INFORM, ActionType.LOAN_REQUEST);
    private Map<String, Double> receivedOffers = new HashMap<>();

    @Override
    public MessageTemplate getTemplate() {
        return template;
    }

    public void restart() {
        receivedOffers = new HashMap<>();
    }

    @Override
    public void actionOnReceive(ACLMessage message) {
        String agent = message.getSender().getLocalName();
        double interest;
        try {
            interest = Double.parseDouble(message.getContent());
        }
        catch (NumberFormatException exception) {
            return;
        }

        receivedOffers.put(agent, interest);
    }

    public boolean allReceived(int numberOfAgents) {
        return numberOfAgents > 0 && numberOfAgents == receivedOffers.size();
    }

    public String getHighestOffer() {
        double offer, highestOffer = 0;
        String highestOfferSender = null;
        for (String sender : receivedOffers.keySet()) {
            offer = receivedOffers.get(sender);
            if (offer > highestOffer) {
                highestOffer = offer;
                highestOfferSender = sender;
            }
        }
        return highestOfferSender;
    }
}
