package stockmarket.behaviours.managers.messages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import com.google.gson.Gson;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.OracleAgent;
import stockmarket.behaviours.managers.protocols.StockMarketManager;
import stockmarket.utils.Utils;

public class OracleNewDayListener extends NewDayListener {
    private static Gson gson = new Gson();
    private static int TIP_DAYS = 3;
    private static int NUMBER_OF_TIPS = 10;
    private final OracleAgent oracle;
    private final Map<String, Map<String, Double>> stockPrices;

    public OracleNewDayListener(OracleAgent oracle) {
        this.oracle = oracle;
        stockPrices = StockMarketManager.loadStockPrices(oracle);
    }

    @Override
    public ACLMessage getReply(ACLMessage message) {
        List<String> agents = oracle.getAgents();
        Collections.shuffle(agents);
        List<String> receivers = agents.subList(0, agents.size() / 2);

        List<String> allCompanies = new ArrayList<>(stockPrices.get("0").keySet());
        Map<String, Map<String, Double>> tips = new HashMap<>();
        Map<String, Double> dailyTips;
        String company;
        int randomCompanyIndex;
        Random random = new Random();
        for (int dayOfTheTip = day + 1; dayOfTheTip < day + TIP_DAYS; ++ dayOfTheTip) {
            dailyTips = new HashMap<>();
            while (dailyTips.size() < NUMBER_OF_TIPS) {
                randomCompanyIndex = random.nextInt(allCompanies.size());
                company = allCompanies.get(randomCompanyIndex);
                dailyTips.put(company, stockPrices.get(String.valueOf(dayOfTheTip)).get(company));
            }
            tips.put(String.valueOf(dayOfTheTip), dailyTips);
        }

        return Utils.createOracleTipMessage(receivers, gson.toJson(tips));
    }
}
