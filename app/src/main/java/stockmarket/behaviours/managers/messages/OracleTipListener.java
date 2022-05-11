package stockmarket.behaviours.managers.messages;

import java.util.HashMap;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class OracleTipListener implements MessageListener {
    private final static MessageTemplate template = Utils.getMessageTemplate(null, ACLMessage.INFORM, ActionType.ORACLE_TIP);
    private static final Gson gson = new Gson();
	private final Map<String, Map<String, Double>> tips = new HashMap<>();

    public Map<String, Double> getTipsForTheDay(int day) {
        String dayString = String.valueOf(day);
        if (tips.containsKey(dayString)) {
            return tips.get(dayString);
        }
        return new HashMap<>();
    }

    public void removeDayFromTips(int day) {
        tips.remove(String.valueOf(day));
    }

    public Map<String, Map<String, Double>> getTips() {
        return tips;
    }

    @Override
    public MessageTemplate getTemplate() {
        return template;
    }

    @Override
    public void actionOnReceive(ACLMessage message) {
        Map<String, Map<String, Double>> tipsReceived;
        try {
            tipsReceived = gson.fromJson(message.getContent(), Map.class);
        }
        catch (JsonSyntaxException exception) {
            return;
        }

        for (String tipDay : tipsReceived.keySet()) {
            if (!tips.containsKey(tipDay)) {
                tips.put(tipDay, new HashMap<>());
            }
            for (String tipCompany : tipsReceived.get(tipDay).keySet()) {
                tips.get(tipDay).put(tipCompany, tipsReceived.get(tipDay).get(tipCompany));
            }
        }
    }
}
