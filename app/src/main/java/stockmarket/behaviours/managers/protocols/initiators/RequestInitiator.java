package stockmarket.behaviours.managers.protocols.initiators;

import java.util.Set;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import stockmarket.utils.Action;

public class RequestInitiator extends Initiator {
	public RequestInitiator(Set<String> receivers, Action action) {
		super(receivers, action);
	}

	public ACLMessage getMessage() {
		return getMessage(FIPANames.InteractionProtocol.FIPA_REQUEST, ACLMessage.REQUEST);
	}

	public void handleResult(ACLMessage result) {}
}
