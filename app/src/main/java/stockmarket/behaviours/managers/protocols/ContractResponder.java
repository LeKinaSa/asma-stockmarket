package stockmarket.behaviours.managers.protocols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.agents.NormalAgent;
import stockmarket.behaviours.managers.Listener;
import stockmarket.behaviours.protocols.RequestInitiatorBehaviour;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Loan;
import stockmarket.utils.MoneyTransfer;
import stockmarket.utils.Utils;

public class ContractResponder implements Listener {
	private final static MessageTemplate template = Utils.getMessageTemplate(
		FIPANames.InteractionProtocol.FIPA_CONTRACT_NET, ACLMessage.CFP, null
	);
	private final Map<String, List<MoneyTransfer>> loans = new HashMap<>();
	private final NormalAgent agent;

	public ContractResponder(NormalAgent agent) {
		this.agent = agent;
	}

	public MessageTemplate getTemplate() {
		return template;
	}

	public void performAction(ACLMessage message) {
		Loan loan = Utils.getLoanFromJson(message.getContent());
		if (loan == null) {
			Utils.log(agent, Utils.invalidAction("Invalid Loan"));
			return;
		}

		String sender = message.getSender().getLocalName();
		Set<String> receivers = new HashSet<>();
		receivers.add(sender);
		ACLMessage over = Utils.createFinishedMessage(receivers);

		if (loan.getAmount() > 0) {
			// Loan Accepted
			MoneyTransfer transfer = new MoneyTransfer(sender, loan.getAmount());
			agent.addBehaviour(new RequestInitiatorBehaviour(
				agent,
				new Initiator(
					agent.getEnvironmentAgents(),
					new Action(ActionType.TRANSFER_MONEY, transfer.toString()),
					over
				)
			));
		}
		else {
			// Loan Denied
			agent.invest(over);
		}
	}

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
}
