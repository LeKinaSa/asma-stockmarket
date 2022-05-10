package stockmarket.agents;

import java.util.HashSet;
import java.util.Set;
import jade.core.Agent;
import stockmarket.behaviours.MessageListenerBehaviour;
import stockmarket.behaviours.SubscriptionInitiatorBehaviour;
import stockmarket.behaviours.managers.messages.OracleNewDayListener;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class OracleAgent extends Agent {
	private final Set<String> normalAgents = new HashSet<>();
	private final OracleNewDayListener oracleNewDayListener = new OracleNewDayListener(this);

	public void setup() {
		// Register
		Utils.registerInYellowPages(this, AgentType.ORACLE);

		// Subscriptions
		addBehaviour(new SubscriptionInitiatorBehaviour(this, AgentType.NORMAL, normalAgents));

		// Repetitive Behaviours
		addBehaviour(new MessageListenerBehaviour(this, oracleNewDayListener));

		Utils.log(this, "Ready");
	}

    public void takedown() {
        Utils.unregisterFromYellowPages(this);
    }

	public Set<String> getAgents() {
		return normalAgents;
	}
}
