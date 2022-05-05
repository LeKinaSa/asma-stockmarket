package stockmarket;

import java.util.Arrays;
import java.util.List;
import jade.core.Agent;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.listeners.messages.OracleNewDayListener;
import stockmarket.utils.Utils;

public class Oracle extends Agent {
	private OracleNewDayListener oracleNewDayListener = new OracleNewDayListener();
	private List<String> receivers = Arrays.asList("a1", "a2"); // TODO: fix this magic

	public void setup() {
		// Repetitive Behaviours
		oracleNewDayListener.updateAgents(receivers);
		addBehaviour(new MessageListenerBehaviour(this, oracleNewDayListener));

		Utils.log(this, "Ready");
	}
}
