package stockmarket.behaviours.managers.messages;

import java.util.HashMap;
import java.util.Map;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.NormalAgent;
import stockmarket.behaviours.managers.protocols.ContractResponder;
import stockmarket.behaviours.managers.protocols.Initiator;
import stockmarket.behaviours.protocols.RequestInitiatorBehaviour;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class NormalAgentNewDayListener extends NewDayListener {
	private final NormalAgent agent;
    private final OracleTipListener tipListener;
    private final ContractResponder loanListener;

    public NormalAgentNewDayListener(NormalAgent agent, OracleTipListener tipListener, ContractResponder loanListener) {
        this.agent = agent;
        this.tipListener = tipListener;
        this.loanListener = loanListener;
    }

    @Override
    public void actionOnReceive(ACLMessage message) {
        super.actionOnReceive(message);

        // TODO: sell all the stocks
        // TODO: pay loans
        // TODO: check current stock prices
        // TODO: decide next investment, based on the tips (stock profit / number of days for return)
        // TODO: ask for permission to loan


        // Decide Next Investments

        // Get the Current Stocks
        Map<String, Double> currentStocks = new HashMap<>();
        agent.addBehaviour(new RequestInitiatorBehaviour(
            agent, new Initiator(
                agent.getEnvironmentAgents(),
                new Action(ActionType.CHECK_STOCK_PRICES),
                null // TODO: check
            )
        ));

        // Get the Tips
        Map<String, Map<String, Double>> tips = tipListener.getTips();

        // Decide what is Best Profit
        double currentPrice, futurePrice;
        double profit, bestProfit = 0;
        String bestCompany = "";
        for (String company : currentStocks.keySet()) {
            currentPrice = currentStocks.get(company);
            for (String dayString : tips.keySet()) {
                futurePrice = tips.get(dayString).get(company);
                profit = futurePrice / currentPrice;
                if (profit > bestProfit) {
                    bestProfit = profit;
                    bestCompany = company;
                }
            }
        }

        agent.setInvestments(bestCompany, bestProfit);
        Utils.createAskForLoanPermissionMessage(agent.getEnvironmentAgents(), bestProfit);
        agent.send(message);
    }
}
