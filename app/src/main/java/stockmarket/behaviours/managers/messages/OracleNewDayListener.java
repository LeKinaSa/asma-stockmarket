package stockmarket.behaviours.managers.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import com.google.gson.Gson;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.OracleAgent;
import stockmarket.behaviours.managers.protocols.StockMarketManager;
import stockmarket.utils.Utils;

public class OracleNewDayListener extends NewDayListener {
    private static Gson gson = new Gson();
    private static int TIP_DAYS = 3;
    private static int NUMBER_OF_TIPS = 10;
    private static int AGENTS_TO_TIPS = 1;
    private final OracleAgent oracle;
    private final Map<String, Map<String, Double>> stockPrices;

    public OracleNewDayListener(OracleAgent oracle) {
        this.oracle = oracle;
        stockPrices = StockMarketManager.loadStockPrices(oracle);
    }

    @Override
    public ACLMessage getReply(ACLMessage message) {
        Random random = new Random();
        int randomIndex;

        List<String> agents = oracle.getAgents();
        Set<String> receivers = new HashSet<>();
        String agent;
        while (receivers.size() < AGENTS_TO_TIPS) {
            randomIndex = random.nextInt(agents.size());
            agent = agents.get(randomIndex);
            receivers.add(agent);
        }

        List<String> allCompanies = new ArrayList<>(stockPrices.get("0").keySet());
        Map<String, Map<String, Double>> tips = new HashMap<>();
        Map<String, Double> dailyTips;
        String company;
        for (int dayOfTheTip = day + 1; dayOfTheTip < day + TIP_DAYS; ++ dayOfTheTip) {
            dailyTips = new HashMap<>();
            while (dailyTips.size() < NUMBER_OF_TIPS) {
                randomIndex = random.nextInt(allCompanies.size());
                company = allCompanies.get(randomIndex);
                dailyTips.put(company, stockPrices.get(String.valueOf(dayOfTheTip)).get(company));
            }
            tips.put(String.valueOf(dayOfTheTip), dailyTips);
        }

        return Utils.createOracleTipMessage(new ArrayList<>(receivers), gson.toJson(tips));
    }
}
