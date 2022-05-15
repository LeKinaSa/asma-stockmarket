package stockmarket.managers.messages;

import java.util.HashMap;
import java.util.Map;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class OracleTipListener implements MessageListener {
    private final static MessageTemplate template = Utils.getMessageTemplate(null, ACLMessage.INFORM, ActionType.ORACLE_TIP);
    private final Map<String, Map<String, Double>> tips = new HashMap<>();

    @Override
    public MessageTemplate getTemplate() {
        return template;
    }

    @Override
    public void actionOnReceive(ACLMessage message) {
        Map<String, Map<String, Double>> tipsReceived = Utils.getDoubleMapFromJson(message.getContent());
        if (tipsReceived == null) {
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

    public void removeDayFromTips(int day) {
        tips.remove(String.valueOf(day));
    }

    public Map<String, Map<String, Double>> getTips() {
        return tips;
    }
}
