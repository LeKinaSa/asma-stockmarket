package stockmarket.listeners.messages;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class OracleTipListener implements MessageListener {
    private final static MessageTemplate template = Utils.getMessageTemplate(null, ACLMessage.INFORM, ActionType.ORACLE_TIP);

    @Override
    public MessageTemplate getTemplate() {
        return template;
    }

    @Override
    public void actionOnReceive(ACLMessage message) {
        System.out.println("Oracle Tip Received"); // TODO: receive oracle tip
    }
}
