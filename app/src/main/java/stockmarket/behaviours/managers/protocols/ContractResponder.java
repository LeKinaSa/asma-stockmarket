package stockmarket.behaviours.managers.protocols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.behaviours.managers.Listener;
import stockmarket.utils.MoneyTransfer;
import stockmarket.utils.Utils;

public class ContractResponder implements Listener {
	private final static MessageTemplate template = Utils.getMessageTemplate(
		FIPANames.InteractionProtocol.FIPA_CONTRACT_NET, ACLMessage.CFP, null
	);
	private final Map<String, List<MoneyTransfer>> loans = new HashMap<>();

    public List<MoneyTransfer> getLoansForTheDay(int day) {
        String dayString = String.valueOf(day);
        if (loans.containsKey(dayString)) {
            return loans.get(dayString);
        }
        return new ArrayList<>();
    }

    public void removeDayFromLoans(int day) {
        loans.remove(String.valueOf(day));
    }

	public MessageTemplate getTemplate() {
		return template;
	}

	public int evaluateAction() {
		// TODO
		return 3;
	}

	public boolean performAction() {
		// TODO
		return true;
	}
}
