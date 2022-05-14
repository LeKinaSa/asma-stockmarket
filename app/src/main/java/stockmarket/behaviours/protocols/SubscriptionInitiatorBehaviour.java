package stockmarket.behaviours.protocols;

import java.util.Set;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.lang.acl.ACLMessage;
import jade.proto.SubscriptionInitiator;
import stockmarket.utils.AgentType;
import stockmarket.utils.Utils;

public class SubscriptionInitiatorBehaviour extends SubscriptionInitiator {
    private final AgentType type;
    private final Set<String> services;

    public SubscriptionInitiatorBehaviour(Agent agent, AgentType type, Set<String> services) {
        super(
            agent,
            DFService.createSubscriptionMessage(
                agent, agent.getDefaultDF(),
                Utils.getRegisterTemplate(type),
                new SearchConstraints()
            )
        );
        this.type = type;
        this.services = services;
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        Utils.log(myAgent, "Notification received from DF");

        try {
            SearchConstraints search = new SearchConstraints();
            search.setMaxResults(-1L);
            DFAgentDescription[] results = DFService.search(myAgent, Utils.getRegisterTemplate(type), search);

            Utils.searchInYellowPageResults(myAgent, type, services, results);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }
}
