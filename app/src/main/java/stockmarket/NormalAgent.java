package stockmarket;

import java.util.Date;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPANames;

import stockmarket.behaviors.RequestInitiatorBehavior;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class NormalAgent extends Agent {
	public void setup() {
		Utils.log(this, "Ready");

		String[] receivers = { "bank", "stock" };
		Action action = new Action(ActionType.START);
		ACLMessage message = getMessage(receivers, action);
		addBehaviour(new RequestInitiatorBehavior(this, null, message, 2));
	}

	public ACLMessage getMessage(String[] receivers, Action action) {
		ACLMessage message = Utils.createMessage(
			FIPANames.InteractionProtocol.FIPA_REQUEST,
			ACLMessage.REQUEST, action.getMessage(), receivers,
			new Date(System.currentTimeMillis() + 10000) // Reply in 10s
		);
		return message;
	}
}
