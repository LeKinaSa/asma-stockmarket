package stockmarket.behaviours;

import java.util.Map;
import java.util.Queue;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import stockmarket.agents.NormalAgent;
import stockmarket.behaviours.managers.protocols.Initiator;

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
        Map<String, Double> currentStocks = agent.getStocks(); // TODO

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

        agent.addBehaviour(initiator.getAfter());
    }
}
