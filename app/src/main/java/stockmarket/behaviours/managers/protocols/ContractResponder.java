package stockmarket.behaviours.managers.protocols;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import jade.core.behaviours.Behaviour;
import jade.domain.FIPANames;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import stockmarket.agents.NormalAgent;
import stockmarket.behaviours.SendMessageBehaviour;
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
		Queue<Behaviour> queuedBehaviours = new LinkedList<>();

		if (loan.getAmount() > 0) {
			// Loan Accepted -> Transfer the Money
			MoneyTransfer transfer = new MoneyTransfer(sender, loan.getAmount());
			queuedBehaviours.add(
				new RequestInitiatorBehaviour(
					agent,
					new Initiator(
						agent.getEnvironmentAgents(),
						new Action(ActionType.TRANSFER_MONEY, transfer.toString()),
						queuedBehaviours
					)
				)
			);
		}
		else {
			// Loan Denied -> Buy Stocks
			queuedBehaviours.add(
				new RequestInitiatorBehaviour(
					agent,
					new Initiator(
						agent.getEnvironmentAgents(),
						new Action(ActionType.BUY_STOCK, agent.getInvestmentCompany()),
						queuedBehaviours
					)
				)
			);
		}

		// Send Finished Message
		Set<String> receivers = new HashSet<>(Arrays.asList(sender));
		queuedBehaviours.add(
			new SendMessageBehaviour(
				agent,
				Utils.createFinishedMessage(receivers),
				new Initiator(queuedBehaviours)
			)
		);

		// Start the Behaviours
		agent.addBehaviour(queuedBehaviours.remove());
	}
}
