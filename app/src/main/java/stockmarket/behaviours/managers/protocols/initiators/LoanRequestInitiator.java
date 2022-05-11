package stockmarket.behaviours.managers.protocols.initiators;

import java.util.Set;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.NormalAgent;
import stockmarket.behaviours.ContractNetInitiatorBehaviour;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class LoanRequestInitiator extends RequestInitiator {
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
                    new Action(ActionType.LOAN_MONEY, "TODO") // TODO
                )
            ));
        }
        else {
            agent.send(Utils.createDayOverMessage(agent.getTimeAgents(), day));
        }
    }
}
