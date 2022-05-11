package stockmarket.behaviours.managers.messages;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.agents.NormalAgent;
import stockmarket.behaviours.managers.protocols.initiators.ContractInitiator;
import stockmarket.behaviours.protocols.ContractNetInitiatorBehaviour;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class GivePermissionListener implements MessageListener {
    private static final MessageTemplate template = Utils.getMessageTemplate(null, ACLMessage.INFORM, ActionType.ASK_PERMISSION);
    private final NormalAgent agent;

    public GivePermissionListener(NormalAgent agent) {
        this.agent = agent;
    }

    @Override
    public MessageTemplate getTemplate() {
        return template;
    }
    
    @Override
    public ACLMessage getReply(ACLMessage message) {
        String sender = message.getSender().getLocalName();
        if (!agent.getLocalName().equals(sender)) {
            return Utils.createDayOverMessage(agent.getEnvironmentAgents(), agent.getDay());
        }

        // Agent has Permission to Get Loans from Other Agents
        agent.addBehaviour(new ContractNetInitiatorBehaviour(
            agent,
            new ContractInitiator(
                agent.getNormalAgents(),
                new Action(ActionType.LOAN_MONEY, "")
            )
        ));
        return null;
    }
}

/*
package stockmarket.behaviours.managers.protocols.initiators;

import java.util.Set;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.NormalAgent;
import stockmarket.behaviours.protocols.ContractNetInitiatorBehaviour;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class LoanRequestInitiator extends RequestInitiator { // TODO: remove and move code somewhere else
    private final NormalAgent agent;
    private final int day;
    private final double bestProfit;
    private final String bestCompany;
    private final String bestDay;

    public LoanRequestInitiator(Set<String> orderAgents, NormalAgent agent, int day,
            double bestProfit, String bestCompany, String bestDay) {
        super(orderAgents, new Action(ActionType.LOAN_REQUEST, String.valueOf(bestProfit)));
        this.agent = agent;
        this.day = day;
        this.bestProfit = bestProfit;
        this.bestCompany = bestCompany;
        this.bestDay = bestDay;
    }

    public void handleResult(ACLMessage message) {
        if (agent.getLocalName().equals(message.getContent())) {
            // Agent has been granted permission to ask other agents for loan
            agent.addBehaviour(new ContractNetInitiatorBehaviour(
                agent, new ContractInitiator(
                    agent.getNormalAgents(),
                    new Action(ActionType.LOAN_MONEY, "TODO") // TODO: information inside contract net initiator content
                )
            ));
        }
        else {
            agent.send(Utils.createDayOverMessage(agent.getEnvironmentAgents(), day));
        }
    }
}
*/
