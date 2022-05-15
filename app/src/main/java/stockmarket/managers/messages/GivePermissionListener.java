package stockmarket.managers.messages;

import java.util.LinkedList;
import java.util.Queue;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.agents.NormalAgent;
import stockmarket.behaviours.SendMessageBehaviour;
import stockmarket.behaviours.protocols.LoanContractNetInitiatorBehaviour;
import stockmarket.behaviours.protocols.RequestInitiatorBehaviour;
import stockmarket.managers.protocols.Initiator;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class GivePermissionListener implements MessageListener {
    private static final MessageTemplate template = Utils.getMessageTemplate(null, ACLMessage.INFORM, ActionType.GIVE_PERMISSION);
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
        String authorizedAgent = message.getContent();

        Queue<Behaviour> queuedBehaviours = new LinkedList<>();
        Initiator initiator = new Initiator(queuedBehaviours);

        // Check Balance
        queuedBehaviours.add(
            new RequestInitiatorBehaviour(
                agent,
                new Initiator(
                    agent.getEnvironmentAgents(),
                    new Action(ActionType.CHECK_BALANCE),
                    queuedBehaviours
                )
            )
        );

        if (!agent.getLocalName().equals(authorizedAgent)) {
            // Agent doesn't have Permission to Get Loans
            queuedBehaviours.add(
                new SendMessageBehaviour(
                    agent,
                    Utils.createDayOverMessage(
                        agent.getEnvironmentAgents(),
                        agent.getDay()
                    ),
                    initiator
                )
            );

            // Start the Behaviours
            initiator.activateNextBehaviour(agent);
            return;
        }

        // Agent has Permission to Get Loans from Other Agents
        // Loan Contract
        queuedBehaviours.add(
            new LoanContractNetInitiatorBehaviour(
                agent,
                new Initiator(
                    agent.getNormalAgents(),
                    new Action(ActionType.LOAN_MONEY),
                    queuedBehaviours
                )
            )
        );

        // Invest -> Buy Stocks
        queuedBehaviours.add(
            new RequestInitiatorBehaviour(
                agent,
                new Initiator(
                    agent.getEnvironmentAgents(),
                    new Action(ActionType.BUY_STOCK, agent.getInvestmentCompany()),
                    queuedBehaviours
                )
            )
        );

        // End the Day for the Agent -> Send the Day Over Message
        queuedBehaviours.add(
            new SendMessageBehaviour(
                agent,
                Utils.createDayOverMessage(
                    agent.getEnvironmentAgents(),
                    agent.getDay()
                ),
                initiator
            )
        );

        // Start the Behaviours
        initiator.activateNextBehaviour(agent);
    }
}
