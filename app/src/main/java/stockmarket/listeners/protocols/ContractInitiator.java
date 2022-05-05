package stockmarket.listeners.protocols;

import java.util.Date;
import java.util.List;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import stockmarket.utils.Action;
import stockmarket.utils.Utils;

public class ContractInitiator {
	public ACLMessage getMessage(List<String> receivers, Action action) {
		ACLMessage message = Utils.createMessage(
			FIPANames.InteractionProtocol.FIPA_CONTRACT_NET, ACLMessage.CFP,
			action.getType(), action.getInformation(),
			receivers, new Date(System.currentTimeMillis() + 10000) // Reply in 10s
		);
		return message;
	}
}
