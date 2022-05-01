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

		addBehaviour(new RequestInitiatorBehavior(this, null, getMessageToBank(), 1));
	}

	public ACLMessage getMessageToBank() {
		String[] receivers = { "bank" };
		ACLMessage message = Utils.createMessage(
			FIPANames.InteractionProtocol.FIPA_REQUEST,
			ACLMessage.REQUEST, "start-bank-account", receivers,
			new Date(System.currentTimeMillis() + 10000) // Reply in 10s
		);
		return message;
	}
}
