package stockmarket.behaviours.managers.messages;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.agents.OracleAgent;
import stockmarket.behaviours.managers.protocols.StockMarketManager;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class OracleNewDayListener implements MessageListener {
    private static final MessageTemplate template = Utils.getMessageTemplate(null, ACLMessage.INFORM, ActionType.NEW_DAY);
    
    private final OracleAgent oracle;
    private final Map<String, Map<String, Double>> stockPrices;

    public OracleNewDayListener(OracleAgent oracle) {
        this.oracle = oracle;
        stockPrices = StockMarketManager.loadStockPrices(oracle);
    }

    @Override
    public MessageTemplate getTemplate() {
        return template;
    }

    @Override
    public ACLMessage getReply(ACLMessage message) {
        List<String> agents = oracle.getAgents();
        Collections.shuffle(agents);
        List<String> receivers = agents.subList(0, agents.size() / 2);

        // TODO: prepare oracle tip to send

        return Utils.createOracleTipMessage(receivers, "oracle-tip-placeholder");
    }
}
