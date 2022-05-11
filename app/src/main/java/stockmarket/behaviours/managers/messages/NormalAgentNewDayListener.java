package stockmarket.behaviours.managers.messages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.NormalAgent;
import stockmarket.behaviours.RequestInitiatorBehaviour;
import stockmarket.behaviours.managers.protocols.initiators.LoanRequestInitiator;
import stockmarket.behaviours.managers.protocols.initiators.RequestInitiator;
import stockmarket.behaviours.managers.protocols.responders.ContractResponder;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.MoneyTransfer;

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

        // Check which Stocks we possess
        // TODO: check which stocks we possess
        Map<String, Double> ownedStocks = new HashMap<>(); // TODO

        // Collect Money from stocks that "end" that day and that we possess
        Map<String, Double> tipsForTheDay = tipListener.getTipsForTheDay(day);
        double nStocks;
        for (String stock : tipsForTheDay.keySet()) {
            if (ownedStocks.containsKey(stock)) {
                nStocks = ownedStocks.get(stock);
                agent.addBehaviour(new RequestInitiatorBehaviour(
                    agent, new RequestInitiator(
                        agent.getStockAgents(), new Action(ActionType.BUY_STOCK, String.valueOf(-nStocks))
                    )
                ));
            }
        }
        tipListener.removeDayFromTips(day);

        // Pay Loans that "end" that day
        List<MoneyTransfer> loansForTheDay = loanListener.getLoansForTheDay(day);
        for (MoneyTransfer loan : loansForTheDay) {
            agent.addBehaviour(new RequestInitiatorBehaviour(
                agent, new RequestInitiator(
                    agent.getBankAgents(), new Action(ActionType.TRANSFER_MONEY, loan.toString())
                )
            ));
        }
        loanListener.removeDayFromLoans(day);

        // TODO: check if we need this sleep
        // Utils.sleep(2); // Wait for Oracle Tips and Money Transfers

        // Decide Next Investments

        // Get the Current Stocks
        Map<String, Double> currentStocks = new HashMap<>();
        agent.addBehaviour(new RequestInitiatorBehaviour(
            agent, new RequestInitiator(
                agent.getStockAgents(), new Action(ActionType.CHECK_STOCK_PRICES)
            )
        ));

        // Get the Tips
        Map<String, Map<String, Double>> tips = tipListener.getTips();

        // Decide what is Best Profit
        double currentPrice, futurePrice;
        double profit, bestProfit = 0;
        String bestCompany = "", bestDay = "0";
        for (String company : currentStocks.keySet()) {
            currentPrice = currentStocks.get(company);
            for (String dayString : tips.keySet()) {
                futurePrice = tips.get(dayString).get(company);
                profit = futurePrice / currentPrice;
                if (profit > bestProfit) {
                    bestProfit = profit;
                    bestCompany = company;
                    bestDay = dayString;
                }
            }
        }

        agent.addBehaviour(new RequestInitiatorBehaviour(
            agent, new LoanRequestInitiator(agent.getOrderAgents(), agent, day, bestProfit, bestCompany, bestDay)
        ));
    }
}
