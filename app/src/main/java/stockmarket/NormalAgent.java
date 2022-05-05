package stockmarket;

import java.util.Arrays;
import java.util.List;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.RequestInitiatorBehaviour;
import stockmarket.listeners.messages.NewDayListener;
import stockmarket.listeners.messages.OracleTipListener;
import stockmarket.listeners.protocols.RequestInitiator;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class NormalAgent extends Agent {
	private NewDayListener newDayListener = new NewDayListener();
	private OracleTipListener oracleTipListener = new OracleTipListener();
	private RequestInitiator initiator = new RequestInitiator();
	private boolean readyToChangeDay = true;
	private List<String> dayOverReceivers = Arrays.asList("time"); // TODO: fix this magic

	public void setup() {
		Utils.log(this, "Ready");

		List<String> receivers = Arrays.asList("bank", "stockmarket"); // TODO: fix this magic
		Action action = new Action(ActionType.START, "{}");
		ACLMessage message = initiator.getMessage(receivers, action);
		addBehaviour(new RequestInitiatorBehaviour(this, initiator, message, receivers.size()));
		addBehaviour(new MessageListenerBehaviour(this, newDayListener));
		addBehaviour(new MessageListenerBehaviour(this, oracleTipListener));
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
}
