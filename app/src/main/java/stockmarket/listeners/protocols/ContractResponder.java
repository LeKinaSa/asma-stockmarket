package stockmarket.listeners.protocols;

import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.listeners.Listener;
import stockmarket.utils.Utils;

public class ContractResponder implements Listener {
	private static MessageTemplate template = Utils.getMessageTemplate(
		FIPANames.InteractionProtocol.FIPA_CONTRACT_NET, ACLMessage.CFP, null
	);

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
