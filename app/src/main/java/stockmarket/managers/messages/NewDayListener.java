package stockmarket.managers.messages;

import java.util.LinkedList;
import java.util.Queue;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.agents.NormalAgent;
import stockmarket.behaviours.DecideInvestmentBehaviour;
import stockmarket.behaviours.protocols.RequestInitiatorBehaviour;
import stockmarket.managers.protocols.Initiator;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.MoneyTransfer;
import stockmarket.utils.Utils;

public class NewDayListener implements MessageListener {
	private final static MessageTemplate template = Utils.getMessageTemplate(null, ACLMessage.INFORM, ActionType.NEW_DAY);
    private final NormalAgent agent;
    private int day;

    @Override
    public MessageTemplate getTemplate() {
        return template;
    }

    public NewDayListener(NormalAgent agent) {
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
        Initiator initiator = new Initiator(queuedBehaviours);

        if (day == 0) {
            Utils.log(agent, "Start Bank");

            queuedBehaviours.add(
                new RequestInitiatorBehaviour(
                    agent,
                    new Initiator(
                        agent.getEnvironmentAgents(),
                        new Action(
                            ActionType.START_BANK,
                            String.valueOf(agent.getInitialMoney())
                        ),
                        queuedBehaviours
                    )
                )
            );
        }

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

        // Decide Next Investment and Ask for Permission to Loan
        queuedBehaviours.add(
            new DecideInvestmentBehaviour(
                agent,
                initiator
            )
        );

        initiator.activateNextBehaviour(agent);
    }
}
