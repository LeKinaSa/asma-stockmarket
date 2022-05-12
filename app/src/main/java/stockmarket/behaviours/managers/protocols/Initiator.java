package stockmarket.behaviours.managers.protocols;

import java.util.Date;
import java.util.Set;
import jade.lang.acl.ACLMessage;
import stockmarket.utils.Action;
import stockmarket.utils.Utils;

public class Initiator {
    private final Set<String> receivers;
	private final Action action;
	private final ACLMessage overMessage;

	public Initiator(Set<String> receivers, Action action, ACLMessage overMessage) {
		this.receivers   = receivers;
		this.action      = action;
		this.overMessage = overMessage;
	}

	public ACLMessage getMessage(String protocol, int performative) {
		ACLMessage message = Utils.createMessage(
            protocol, performative,
			action.getType(), action.getInformation(),
			receivers, new Date(System.currentTimeMillis() + 10000) // Reply in 10s
		);
		return message;
	}

	public int getNResponders() {
		return receivers.size();
	}

	public ACLMessage getOverMessage() {
		return this.overMessage;
	}
}
