package stockmarket.behaviours.managers.protocols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
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
	private final Gson gson = new Gson();
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
		Loan loan;
		try {
			loan = gson.fromJson(message.getContent(), Loan.class);
		}
		catch (JsonSyntaxException exception) {
			Utils.log(agent, Utils.invalidAction("Invalid Loan"));
			return;
		}

		if (loan.getAmount() > 0) {
			// Loan Accepted
			String sender = message.getSender().getLocalName();
			MoneyTransfer transfer = new MoneyTransfer(sender, loan.getAmount());
			agent.addBehaviour(new RequestInitiatorBehaviour(
				agent,
				new Initiator(
					agent.getEnvironmentAgents(),
					new Action(ActionType.TRANSFER_MONEY, gson.toJson(transfer))
				)
			)); // TODO: send message after
		}
		else {
			// Loan Denied
			agent.invest(null);
			// TODO: send message after
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
