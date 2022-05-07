package stockmarket.utils;

import java.util.Date;
import java.util.List;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Utils {
    // Message Related Utilitaries
    public static MessageTemplate getMessageTemplate(String protocol, int performative, ActionType ontology) {
        MessageTemplate template = MessageTemplate.MatchPerformative(performative);
        if (protocol != null) {
            template = MessageTemplate.and(template, MessageTemplate.MatchProtocol(protocol));
        }
        if (ontology != null) {
            template = MessageTemplate.and(template, MessageTemplate.MatchOntology(ontology.toString()));
        }
        return template;
    }

    public static ACLMessage createMessage(
            String protocol, int performative,
            ActionType ontology, String content,
            List<String> receivers, Date date) {

        ACLMessage message = new ACLMessage(performative);
        message.setLanguage(FIPANames.ContentLanguage.FIPA_SL);
        if (protocol != null) {
            message.setProtocol(protocol);
        }
        if (ontology != null) {
            message.setOntology(ontology.toString());
        }
        if (content != null) {
            message.setContent(content);
        }
        if (date != null) {
            message.setReplyByDate(date);
        }

        if (receivers != null) {
            for (String receiver : receivers) {
                message.addReceiver(new AID(receiver, AID.ISLOCALNAME));
            }
        }

        return message;
    }

    public static ACLMessage createNewDayMessage(List<String> receivers, Integer day) {
        return createMessage(
            null, ACLMessage.INFORM,
            ActionType.NEW_DAY, day.toString(),
            receivers, null
        );
    }

    public static ACLMessage createOracleTipMessage(List<String> receivers, String tip) {
        return createMessage(
            null, ACLMessage.INFORM,
            ActionType.ORACLE_TIP, tip,
            receivers, null
        );
    }

    public static ACLMessage createDayOverMessage(List<String> receivers, Integer day) {
        return createMessage(
            null, ACLMessage.INFORM,
            ActionType.DAY_OVER, day.toString(),
            receivers, null
        );
    }

    public static ACLMessage createReply(ACLMessage msg, int performative, String content) {
        ACLMessage reply = msg.createReply();
        reply.setPerformative(performative);
        if (content != null) {
            reply.setContent(content);
        }
        return reply;
    }

    public static String invalidAction(String message) {
        if (message != null && message != "") {
            message = ": " + message;
        }
        else {
            message = "";
        }
        return "Invalid Action" + message + ".";
    }

    // Yellow Page Utilitaries
    public static void registerInYellowPages(Agent agent, AgentType type) {
        try {
            DFAgentDescription register = new DFAgentDescription();
            ServiceDescription service = new ServiceDescription();

            service.setName(agent.getLocalName());
            service.setType(type.toString());
            service.addLanguages(FIPANames.ContentLanguage.FIPA_SL);

            for (String ontology : type.getOntologies()) {
                service.addOntologies(ontology);
            }
            // TODO: Properties: service.addProperties(new Property("country", "Italy"));

            register.setName(agent.getAID());
            register.addServices(service);

            DFService.register(agent, register);
        }
        catch (FIPAException exception) {
            exception.printStackTrace();
        }
    }

    // Sleep
    public static void sleep(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        }
        catch (InterruptedException ignored) {}
    }

    // Logs
    public static void log(Agent agent, String message) {
        System.out.println("Agent " + agent.getLocalName() + ": " + message);
    }

    public static void log(AID agent, String message) {
        System.out.println("Agent " + agent.getLocalName() + ": " + message);
    }
}
