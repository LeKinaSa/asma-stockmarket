package stockmarket;

import java.util.Arrays;
import java.util.List;
import jade.core.Agent;
import stockmarket.agents.OracleNewDayListener;
import stockmarket.behaviors.ListeningBehavior;
import stockmarket.utils.Utils;

public class Oracle extends Agent {
	private OracleNewDayListener oracleNewDayListener = new OracleNewDayListener();
	private List<String> receivers = Arrays.asList("a1", "a2"); // TODO: fix this magic

	public void setup() {
		Utils.log(this, "Ready");

		oracleNewDayListener.updateAgents(receivers);
		addBehaviour(new ListeningBehavior(this, oracleNewDayListener));
	}
}
