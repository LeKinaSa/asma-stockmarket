package stockmarket.behaviours;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import com.google.gson.Gson;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.EnvironmentAgent;
import stockmarket.utils.Utils;

public class EnvironmentBehaviour extends CyclicBehaviour {
    private static final Gson gson = new Gson();
    private static final int AGENTS_TO_TIP = 1;
    private static final int TIP_DAYS = 3;
    private static final int NUMBER_OF_TIPS = 10;
    private final EnvironmentAgent agent;

    public EnvironmentBehaviour(EnvironmentAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        if (agent.getDayListener().canPassToNextDay(agent.getNAgents())) {
            int nextDay = agent.getDayListener().nextDay();
            sendNewDayMessage(nextDay);
            sendOracleTipMessage(nextDay);
        }
    }

    public void sendNewDayMessage(int newDay) {
        ACLMessage message = Utils.createNewDayMessage(agent.getAgents(), newDay);
        agent.send(message);
    }

    public void sendOracleTipMessage(int newDay) {
        Random random = new Random();
        int randomIndex;

        List<String> agents = new ArrayList<>(agent.getAgents());
        Set<String> receivers = new HashSet<>();
        String agentToTip;
        while (receivers.size() < AGENTS_TO_TIP) {
            randomIndex = random.nextInt(agents.size());
            agentToTip = agents.get(randomIndex);
            receivers.add(agentToTip);
        }

        List<String> allCompanies = new ArrayList<>(agent.getStockPrices().get("0").keySet());
        Map<String, Map<String, Double>> tips = new HashMap<>();
        Map<String, Double> dailyTips;
        String company;
        for (int dayOfTheTip = newDay + 1; dayOfTheTip < newDay + TIP_DAYS; ++ dayOfTheTip) {
            dailyTips = new HashMap<>();
            while (dailyTips.size() < NUMBER_OF_TIPS) {
                randomIndex = random.nextInt(allCompanies.size());
                company = allCompanies.get(randomIndex);
                dailyTips.put(company, agent.getStockPrices().get(String.valueOf(dayOfTheTip)).get(company));
            }
            tips.put(String.valueOf(dayOfTheTip), dailyTips);
        }

        ACLMessage message = Utils.createOracleTipMessage(receivers, gson.toJson(tips));
        agent.send(message);
    }
}
