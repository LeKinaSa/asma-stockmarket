package stockmarket.managers.protocols;

import java.util.Date;
import java.util.Queue;
import java.util.Set;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import stockmarket.utils.Action;
import stockmarket.utils.Utils;

public class Initiator {
    private final Set<String> receivers;
    private final Action action;
    private final Queue<Behaviour> after;

    public Initiator(Set<String> receivers, Action action, Queue<Behaviour> after) {
        this.receivers = receivers;
        this.action = action;
        this.after = after;
    }

    public Initiator(Queue<Behaviour> after) {
        this.receivers = null;
        this.action = null;
        this.after = after;
    }

    public ACLMessage getMessage(String protocol, int performative) {
        ACLMessage message = Utils.createMessage(
            protocol, performative,
            action.getType(), action.getInformation(),
            receivers, new Date(System.currentTimeMillis() + 10000) // Reply in 10s
        );
        return message;
    }

    public int getNResponders() {
        return receivers.size();
    }

    private Behaviour getAfter() {
        if (after == null || after.isEmpty()) {
            return null;
        }
        return after.remove();
    }

    public void activateNextBehaviour(Agent agent) {
        Behaviour behaviour = getAfter();
        if (behaviour != null) {
            agent.addBehaviour(behaviour);
        }
    }

    public void addBehaviour(Behaviour behaviour) {
        if (after != null && behaviour != null) {
            after.add(behaviour);
        }
    }
}
