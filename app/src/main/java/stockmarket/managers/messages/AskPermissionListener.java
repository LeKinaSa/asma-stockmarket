package stockmarket.managers.messages;

import java.util.HashMap;
import java.util.Map;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class AskPermissionListener implements MessageListener {
    private static final MessageTemplate template = Utils.getMessageTemplate(null, ACLMessage.INFORM, ActionType.ASK_PERMISSION);
    private final Agent agent;
    private final Map<String, Double> receivedOffers = new HashMap<>();

    public AskPermissionListener(Agent agent) {
        this.agent = agent;
    }

    @Override
    public MessageTemplate getTemplate() {
        return template;
    }

    @Override
    public void actionOnReceive(ACLMessage message) {
        String sender = message.getSender().getLocalName();
        double interest = 0;
        try {
            interest = Double.parseDouble(message.getContent());
        }
        catch (NumberFormatException exception) {
            Utils.error(agent, Utils.invalidAction("Invalid Interest"));
        }

        receivedOffers.put(sender, interest);
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
        receivedOffers.clear(); // Prepare for next round
        return highestOfferSender;
    }
}
