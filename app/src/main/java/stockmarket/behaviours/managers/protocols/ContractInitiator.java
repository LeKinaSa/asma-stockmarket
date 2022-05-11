package stockmarket.behaviours.managers.protocols;

import java.util.Set;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import stockmarket.utils.Action;

public class ContractInitiator extends Initiator {
	public ContractInitiator(Set<String> receivers, Action action) {
		super(receivers, action);
	}

	public ACLMessage getMessage() {
		return getMessage(FIPANames.InteractionProtocol.FIPA_CONTRACT_NET, ACLMessage.CFP);
	}
}
