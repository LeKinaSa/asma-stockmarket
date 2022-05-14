package stockmarket.behaviours;

import java.util.Map;
import jade.core.behaviours.OneShotBehaviour;
import stockmarket.agents.NormalAgent;
import stockmarket.managers.protocols.Initiator;
import stockmarket.utils.Utils;

public class DecideInvestmentBehaviour extends OneShotBehaviour {
    private final NormalAgent agent;
	private final Initiator initiator;

    public DecideInvestmentBehaviour(NormalAgent agent, Initiator initiator) {
        this.agent = agent;
        this.initiator = initiator;
    }

    @Override
    public void action() {
        Map<String, Map<String, Double>> tips = agent.getTips();
        Map<String, Double> currentStocks = agent.getStockPrices();

        double currentPrice, futurePrice;
        double profit, bestProfit = 0;
        String bestCompany = null;
        int day, daysToInvest;
        for (String dayString : tips.keySet()) {
            try {
                day = Integer.parseInt(dayString);
            }
            catch (NumberFormatException exception) {
                Utils.log(agent, "Invalid Tip Day when Deciding Investment");
                continue;
            }
            daysToInvest = day - agent.getDay();
            for (String company : tips.get(dayString).keySet()) {
                currentPrice = currentStocks.get(company);
                futurePrice = tips.get(dayString).get(company);

                profit = ((futurePrice / currentPrice * 100) - 100) / daysToInvest;
                if (profit > bestProfit) {
                    bestProfit = profit;
                    bestCompany = company;
                }
            }
        }

        agent.setInvestments(bestCompany, bestProfit);
        initiator.addBehaviour(new SendMessageBehaviour(
            agent,
            Utils.createAskForLoanPermissionMessage(
                agent.getEnvironmentAgents(),
                agent.getBestInterest()
            ),
            initiator
        ));

        initiator.activateNextBehaviour(agent);
    }
}
