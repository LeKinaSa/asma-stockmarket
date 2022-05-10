package stockmarket.behaviours.managers.protocols;

import java.util.Date;
import java.util.Set;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import stockmarket.utils.Action;
import stockmarket.utils.Utils;

public class RequestInitiator {
	private final Set<String> receivers;
	private final Action action;

	public RequestInitiator(Set<String> receivers, Action action) {
		this.receivers = receivers;
		this.action = action;
	}

	public ACLMessage getMessage() {
		ACLMessage message = Utils.createMessage(
			FIPANames.InteractionProtocol.FIPA_REQUEST, ACLMessage.REQUEST,
			action.getType(), action.getInformation(),
			receivers, new Date(System.currentTimeMillis() + 10000) // Reply in 10s
		);
		return message;
	}

	public int getNResponders() {
		return receivers.size();
	}
}
