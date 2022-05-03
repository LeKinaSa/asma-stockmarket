package stockmarket;

import jade.core.Agent;
import stockmarket.agents.OracleNewDayListener;
import stockmarket.behaviors.ListeningBehavior;
import stockmarket.utils.Utils;

public class Oracle extends Agent {
	private OracleNewDayListener oracleNewDayListener = new OracleNewDayListener();

	public void setup() {
		Utils.log(this, "Ready");

		addBehaviour(new ListeningBehavior(this, oracleNewDayListener));
	}
}
