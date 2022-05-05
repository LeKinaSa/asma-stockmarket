package stockmarket.agents.messages;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class OracleNewDayListener implements Listener {
    private MessageTemplate template = Utils.getMessageTemplate(null, ACLMessage.INFORM, ActionType.NEW_DAY);
    private List<String> agents = Arrays.asList("a1", "a2"); // TODO: fix this magic

    public void updateAgents(List<String> agents) {
        this.agents = agents;
    }

    @Override
    public MessageTemplate getTemplate() {
        return template;
    }

    @Override
    public ACLMessage getReply(ACLMessage message) {
        Collections.shuffle(agents);
        List<String> receivers = agents.subList(0, agents.size() / 2);

        // TODO: prepare oracle tip to send

        return Utils.createOracleTipMessage(receivers, "oracle-tip-placeholder");
    }
}
