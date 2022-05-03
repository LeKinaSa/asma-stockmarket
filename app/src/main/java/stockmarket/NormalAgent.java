package stockmarket;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.domain.FIPANames;
import stockmarket.agents.NewDayListener;
import stockmarket.agents.OracleTipListener;
import stockmarket.behaviors.ListeningBehaviour;
import stockmarket.behaviors.RequestInitiatorBehaviour;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class NormalAgent extends Agent {
	private NewDayListener newDayListener = new NewDayListener();
	private OracleTipListener oracleTipListener = new OracleTipListener();
	boolean readyToChangeDay = true;
	private List<String> dayOverReceivers = Arrays.asList("time"); // TODO: fix this magic

	public void setup() {
		Utils.log(this, "Ready");

		List<String> receivers = Arrays.asList("bank", "stockmarket"); // TODO: fix this magic
		Action action = new Action(ActionType.START, "{}");
		ACLMessage message = getMessage(receivers, action);
		addBehaviour(new RequestInitiatorBehaviour(this, null, message, 2));
		addBehaviour(new ListeningBehaviour(this, newDayListener));
		addBehaviour(new ListeningBehaviour(this, oracleTipListener));
		addBehaviour(new CyclicBehaviour() {
			@Override
			public void action() {
				if (readyToChangeDay) {
					send(Utils.createDayOverMessage(dayOverReceivers, newDayListener.getDay()));
					readyToChangeDay = false;
				}
			}
		});
	}

	public ACLMessage getMessage(List<String> receivers, Action action) {
		ACLMessage message = Utils.createMessage(
			FIPANames.InteractionProtocol.FIPA_REQUEST, ACLMessage.REQUEST,
			action.getType(), action.getInformation(),
			receivers, new Date(System.currentTimeMillis() + 10000) // Reply in 10s
		);
		return message;
	}
}
