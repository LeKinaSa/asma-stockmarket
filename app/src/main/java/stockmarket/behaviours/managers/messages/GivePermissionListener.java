package stockmarket.behaviours.managers.messages;

import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.agents.NormalAgent;
import stockmarket.behaviours.managers.protocols.Initiator;
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
    public void actionOnReceive(ACLMessage message) {
        String sender = message.getSender().getLocalName();
        if (!agent.getLocalName().equals(sender)) {
            ACLMessage reply = Utils.createDayOverMessage(agent.getEnvironmentAgents(), agent.getDay());
            agent.send(reply);
        }

        // Agent has Permission to Get Loans from Other Agents
        agent.addBehaviour(new ContractNetInitiatorBehaviour(
            agent,
            new Initiator(
                agent.getNormalAgents(),
                new Action(ActionType.LOAN_MONEY, "")
            )
        ));
    }
}
