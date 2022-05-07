package stockmarket.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPANames;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
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
    public static DFAgentDescription getRegisterTemplate(AgentType type) {
        ServiceDescription serviceTemplate = new ServiceDescription();
        serviceTemplate.setType(type.toString());
        DFAgentDescription template = new DFAgentDescription();
        template.addServices(serviceTemplate);
        return template;
    }

    public static void registerInYellowPages(Agent agent, AgentType type) {
        try {
            // Service Configuration
            ServiceDescription service = new ServiceDescription();
            service.setName(agent.getLocalName());
            service.setType(type.toString());
            service.addLanguages(FIPANames.ContentLanguage.FIPA_SL);
            for (String ontology : type.getOntologies()) {
                service.addOntologies(ontology);
            }
            // TODO: Properties: service.addProperties(new Property("country", "Italy"));

            // Register Configuration
            DFAgentDescription register = new DFAgentDescription();
            register.setName(agent.getAID());
            register.addServices(service);

            // Register the Agent
            DFService.register(agent, register);
        }
        catch (FIPAException exception) {
            exception.printStackTrace();
        }
    }

    public static void searchInYellowPages(Agent agent, AgentType type, List<String> services) {
        try {
            // Build Template
            DFAgentDescription template = getRegisterTemplate(type);

            // Search for the Specified Agent Type
            DFAgentDescription[] results = DFService.search(agent, template, new SearchConstraints());

            // Select the Services
            searchInResults(agent, type, services, results);
        }
        catch (FIPAException exception) {
            exception.printStackTrace();
        }
    }

    public static void searchInResults(Agent agent, AgentType type, List<String> services, DFAgentDescription[] results) {
        DFAgentDescription register;
        ServiceDescription service;
        AID provider;
        Iterator it;
        if (results.length > 0) {
            Utils.log(agent, "Found Agents from Type " + type);
            for (int i = 0; i < results.length; ++ i) {
                register = results[i];
                provider = register.getName();

                // Select the Service we are looking for
                it = register.getAllServices();
                while (it.hasNext()) {
                    service = (ServiceDescription) it.next();
                    if (service.getType().equals(type.toString())) {
                        services.add(provider.getLocalName());
                        System.out.println("Service " + service.getName() + " provided by agent " + provider.getLocalName()); // TODO: remove later
                    }
                }
            }
        }
        else {
            Utils.log(agent, "No Agent from Type " + type + " Found");
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
