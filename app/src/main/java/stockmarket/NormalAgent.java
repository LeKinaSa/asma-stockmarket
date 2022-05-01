package stockmarket;

import java.util.Date;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPANames;

import stockmarket.behaviors.RequestInitiatorBehavior;
import stockmarket.utils.Utils;

public class NormalAgent extends Agent {
	public void setup() {
		Utils.log(this, "Ready");

		String[] receivers = { "bank", "stock" };
		ACLMessage message = getMessage(receivers, "start");
		addBehaviour(new RequestInitiatorBehavior(this, null, message, 2));
	}

	public ACLMessage getMessage(String[] receivers, String content) {
		ACLMessage message = Utils.createMessage(
			FIPANames.InteractionProtocol.FIPA_REQUEST,
			ACLMessage.REQUEST, content, receivers,
			new Date(System.currentTimeMillis() + 10000) // Reply in 10s
		);
		return message;
	}
}
