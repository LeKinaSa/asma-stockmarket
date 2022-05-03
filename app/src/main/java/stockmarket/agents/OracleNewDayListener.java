package stockmarket.agents;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class OracleNewDayListener implements Listener {
    private MessageTemplate template = MessageTemplate.and(
		MessageTemplate.MatchPerformative(ACLMessage.INFORM),
        MessageTemplate.MatchOntology(ActionType.NEW_DAY.toString())
    );
    private List<String> agents = Arrays.asList("a1", "a2"); // TODO: fix this magic

    @Override
    public MessageTemplate getTemplate() {
        return template;
    }

    @Override
    public ACLMessage getReply(ACLMessage message) {
        Collections.shuffle(agents);
        List<String> receivers = agents.subList(0, agents.size() / 2);

        message = Utils.createOracleTipMessage(receivers, "oracle-tip-placeholder"); // TODO: oracle Tip
        System.out.println("\t\tOracle Sending tip to");
        System.out.println(receivers);
        System.out.println();
        return message;
    }
}
