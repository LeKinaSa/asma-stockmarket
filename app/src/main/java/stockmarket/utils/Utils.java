package stockmarket.utils;

import java.util.Date;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Utils {
    public static ACLMessage createMessage(
            String protocol, int performative,
            ActionType ontology, String content,
            String[] receivers, Date date) {

        ACLMessage message = new ACLMessage(performative);
        if (protocol != null) { message.setProtocol(protocol); }
        if (ontology != null) { message.setOntology(ontology.toString()); }
        if (content != null) { message.setContent(content); }
        if (date != null) { message.setReplyByDate(date); }

        if (receivers != null) {
            for (String receiver : receivers) {
                message.addReceiver(new AID(receiver, AID.ISLOCALNAME));
            }
        }

        return message;
    }

    public static ACLMessage createNewDayMessage(String[] receivers, Integer day) {
        return createMessage(
            null, ACLMessage.INFORM,
            ActionType.NEW_DAY, day.toString(),
            receivers, null
        );
    }

    public static ACLMessage createOracleTipMessage(String[] receivers, String tip) {
        return createMessage(
            null, ACLMessage.INFORM,
            ActionType.ORACLE_TIP, tip,
            receivers, null
        );
    }

    public static ACLMessage createDayOverMessage(String[] receivers, Integer day) {
        return createMessage(
            null, ACLMessage.INFORM,
            ActionType.DAY_OVER, day.toString(),
            receivers, null
        );
    }

    public static ACLMessage createReply(ACLMessage msg, int performative, String content) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(performative);
        if (content != null) { reply.setContent(content); }
        return reply;
    }

    public static MessageTemplate getMessageTemplate(String protocol, int performative) {
        MessageTemplate template = MessageTemplate.and(
            MessageTemplate.MatchProtocol(protocol),
            MessageTemplate.MatchPerformative(performative)
	    );
        return template;
    }

    public static void log(Agent agent, String message) {
        System.out.println("Agent " + agent.getLocalName() + ": " + message);
    }
    public static void log(AID agent, String message) {
        System.out.println("Agent " + agent.getLocalName() + ": " + message);
    }
}
