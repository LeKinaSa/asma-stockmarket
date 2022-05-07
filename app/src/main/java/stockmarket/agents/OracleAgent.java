package stockmarket.agents;

import java.util.ArrayList;
import java.util.List;
import jade.core.Agent;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.SubscriptionInitiatorBehaviour;
import stockmarket.behaviours.managers.messages.OracleNewDayListener;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class OracleAgent extends Agent {
	private final List<String> normalAgents = new ArrayList<>();
	private final OracleNewDayListener oracleNewDayListener = new OracleNewDayListener(this);

	public void setup() {
		// Subscriptions
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.NORMAL, normalAgents));

		// Repetitive Behaviours
		addBehaviour(new MessageListenerBehaviour(this, oracleNewDayListener));

		Utils.log(this, "Ready");
	}

	public List<String> getAgents() {
		return normalAgents;
	}
}
