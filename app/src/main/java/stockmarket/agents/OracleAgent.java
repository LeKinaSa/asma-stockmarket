package stockmarket.agents;

import java.util.Arrays;
import java.util.List;
import jade.core.Agent;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.managers.messages.OracleNewDayListener;
import stockmarket.utils.Utils;

public class OracleAgent extends Agent {
	private final OracleNewDayListener oracleNewDayListener = new OracleNewDayListener(this);
	private List<String> agents = Arrays.asList("a1", "a2"); // TODO: fix this magic

	public void setup() {
		// Repetitive Behaviours
		addBehaviour(new MessageListenerBehaviour(this, oracleNewDayListener));

		Utils.log(this, "Ready");
	}

	public List<String> getAgents() {
		return agents;
	}
}
