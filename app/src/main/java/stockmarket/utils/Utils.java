package stockmarket.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class Utils {
    // Message Related Utilitaries
    public static MessageTemplate getMessageTemplate(String protocol, int performative,
            ActionType ontology) {
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
            Set<String> receivers, Date date) {

        ACLMessage message = new ACLMessage(performative);
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

    public static ACLMessage createNewDayMessage(Set<String> receivers, Integer day) {
        return createMessage(
            null, ACLMessage.INFORM,
            ActionType.NEW_DAY, day.toString(),
            receivers, null
        );
    }

    public static ACLMessage createOracleTipMessage(Set<String> receivers, String tip) {
        return createMessage(
            null, ACLMessage.INFORM,
            ActionType.ORACLE_TIP, tip,
            receivers, null
        );
    }

    public static ACLMessage createDayOverMessage(Set<String> receivers, Integer day) {
        return createMessage(
            null, ACLMessage.INFORM,
            ActionType.DAY_OVER, day.toString(),
            receivers, null
        );
    }

    public static ACLMessage createFinishedMessage(Set<String> receivers) {
        return createMessage(
            null, ACLMessage.INFORM,
            ActionType.FINISHED, null,
            receivers, null
        );
    }

    public static ACLMessage createAskForLoanPermissionMessage(Set<String> receivers, double interest) {
        return createMessage(
            null, ACLMessage.INFORM,
            ActionType.ASK_PERMISSION, String.valueOf(interest),
            receivers, null
        );
    }

    public static ACLMessage createLoanPermissionMessage(Set<String> receivers, String chosenAgent) {
        return createMessage(
            null, ACLMessage.INFORM,
            ActionType.GIVE_PERMISSION, chosenAgent,
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
            for (String ontology : type.getOntologies()) {
                service.addOntologies(ontology);
            }

            // Register Configuration
            DFAgentDescription register = new DFAgentDescription();
            register.setName(agent.getAID());

            // Unregister if the Agent is Already Registered
            DFAgentDescription[] results = DFService.search(agent, register);
            if (results.length > 0) {
                DFService.deregister(agent);
            }

            // Register the Agent with the Service
            register.addServices(service);
            DFService.register(agent, register);
        }
        catch (FIPAException exception) {
            exception.printStackTrace();
        }
    }

    public static void unregisterFromYellowPages(Agent agent) {
        try {
            DFService.deregister(agent);
        }
        catch (FIPAException ignored) {}
    }

    public static void searchInYellowPageResults(Agent agent, AgentType type,
            Set<String> services, DFAgentDescription[] results) {
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
                        Utils.logProvided(service, provider);
                    }
                }
            }
        }
        else {
            Utils.log(agent, "No Agent from Type " + type + " Found");
        }
    }

    // Message Content
    public static final Gson gson = new Gson();

    public static Map<String, Map<String, Double>> loadStockPrices(Agent agent) {
        interface StockPrices extends Map<String, Map<String, Double>> {};
        final File file = new File("data/formatted_stock_prices.json");
        StockPrices stocks = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {
            stocks = gson.fromJson(reader, StockPrices.class);
        }
        catch (JsonSyntaxException | IOException exception) {
            Utils.log(agent, "Error when loading the stockmarket prices history -> " + exception.getMessage());
        }
        return stocks;
    }

    public static Map<String, Map<String, Double>> getMapFromJson(String json) {
        interface StockPrices extends Map<String, Map<String, Double>> {};
        StockPrices map = null;
        try {
            map = Utils.gson.fromJson(json, StockPrices.class);
        }
        catch (JsonSyntaxException ignored) {}
        return map;
    }

    public static StockEntry getStockEntryFromJson(String json) {
        StockEntry entry = null;
        try {
            entry = Utils.gson.fromJson(json, StockEntry.class);
        }
        catch (JsonSyntaxException ignored) {}
        return entry;
    }

    public static MoneyTransfer getTransferFromJson(String json) {
        MoneyTransfer transfer = null;
        try {
            transfer = Utils.gson.fromJson(json, MoneyTransfer.class);
        }
        catch (JsonSyntaxException ignored) {}
        return transfer;
    }

    public static Loan getLoanFromJson(String json) {
        Loan loan = null;
		try {
			loan = gson.fromJson(json, Loan.class);
		}
		catch (JsonSyntaxException ignored) {}
        return loan;
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

    public static void logProvided(ServiceDescription service, AID provider) {
        System.out.println("Service " + service.getName() + " provided by agent " + provider.getLocalName());
    }
}
