package stockmarket.behaviours.managers.messages;

import java.util.LinkedList;
import java.util.Queue;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.agents.NormalAgent;
import stockmarket.behaviours.DecideInvestmentBehaviour;
import stockmarket.behaviours.SendMessageBehaviour;
import stockmarket.behaviours.managers.protocols.Initiator;
import stockmarket.behaviours.protocols.RequestInitiatorBehaviour;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.MoneyTransfer;
import stockmarket.utils.Utils;

public class NormalAgentNewDayListener implements MessageListener {
	private final static MessageTemplate template = Utils.getMessageTemplate(null, ACLMessage.INFORM, ActionType.NEW_DAY);
    private final NormalAgent agent;
    private int day;

    @Override
    public MessageTemplate getTemplate() {
        return template;
    }

    public NormalAgentNewDayListener(NormalAgent agent) {
        this.agent = agent;
    }

    public int getDay() {
        return day;
    }

    @Override
    public void actionOnReceive(ACLMessage message) {
        try {
            day = Integer.parseInt(message.getContent());
        }
        catch (NumberFormatException ignored) {}

        agent.removeDayTips();
        agent.setInvestments(null, 0);

        Queue<Behaviour> queuedBehaviours = new LinkedList<>();

        // Sell Stocks
        queuedBehaviours.add(
            new RequestInitiatorBehaviour(
                agent,
                new Initiator(
                    agent.getEnvironmentAgents(),
                    new Action(ActionType.SELL_STOCK),
                    queuedBehaviours
                )
            )
        );

        // Pay Each of the Loans
        for (MoneyTransfer transfer : agent.getLoans()) {
            queuedBehaviours.add(
                new RequestInitiatorBehaviour(
                    agent,
                    new Initiator(
                        agent.getEnvironmentAgents(),
                        new Action(ActionType.TRANSFER_MONEY, transfer.toString()),
                        queuedBehaviours
                    )
                )
            );
        }

        // Check Current Stock Prices
        queuedBehaviours.add(
            new RequestInitiatorBehaviour(
                agent,
                new Initiator(
                    agent.getEnvironmentAgents(),
                    new Action(ActionType.CHECK_STOCK_PRICES),
                    queuedBehaviours
                )
            )
        );

        // Decide Next Investment
        queuedBehaviours.add(
            new DecideInvestmentBehaviour(
                agent,
                new Initiator(queuedBehaviours)
            )
        );

        // Ask for Permission to Loan
        queuedBehaviours.add(
            new SendMessageBehaviour(
                agent,
                Utils.createAskForLoanPermissionMessage(
                    agent.getEnvironmentAgents(),
                    agent
                ),
                new Initiator(queuedBehaviours)
            )
        );
    }
}
