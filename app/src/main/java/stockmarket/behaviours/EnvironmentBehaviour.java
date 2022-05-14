package stockmarket.behaviours;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.EnvironmentAgent;
import stockmarket.behaviours.managers.protocols.Initiator;
import stockmarket.behaviours.protocols.RequestInitiatorBehaviour;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class EnvironmentBehaviour extends CyclicBehaviour {
    private static final int AGENTS_TO_TIP  = 1;
    private static final int TIP_DAYS       = 3;
    private static final int NUMBER_OF_TIPS = 10;
    private final EnvironmentAgent agent;

    public EnvironmentBehaviour(EnvironmentAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        if (agent.getDayListener().canPassToNextDay(agent.getNAgents())) {
            int nextDay = agent.getDayListener().nextDay();
            Utils.log(agent, "Starting Day " + nextDay);
            if (agent.simulationIsOver(nextDay)) {
                endSimulation();
                return;
            }
            startDay(nextDay);
        }
    }

    public void startDay(int nextDay) {
        Queue<Behaviour> queuedBehaviours = new LinkedList<>();
        Initiator initiator = new Initiator(queuedBehaviours);

        queuedBehaviours.add(
            new SendMessageBehaviour(
                agent,
                getOracleTipMessage(nextDay),
                initiator
            )
        );
        queuedBehaviours.add(
            new SendMessageBehaviour(
                agent,
                getNewDayMessage(nextDay),
                initiator
            )
        );

        initiator.activateNextBehaviour(agent);
    }

    public void endSimulation() {
        Queue<Behaviour> queuedBehaviours = new LinkedList<>();
        Initiator initiator = new Initiator(queuedBehaviours);

        queuedBehaviours.add(
            new RequestInitiatorBehaviour(
                agent,
                new Initiator(
                    agent.getAgents(),
                    new Action(ActionType.END_SIMULATION),
                    queuedBehaviours
                )
            )
        );

        queuedBehaviours.add(
            new TakeDownBehaviour(agent)
        );

        initiator.activateNextBehaviour(agent);
    }

    private ACLMessage getNewDayMessage(int newDay) {
        return Utils.createNewDayMessage(agent.getAgents(), newDay);
    }

    private ACLMessage getOracleTipMessage(int newDay) {
        Random random = new Random();
        int randomIndex;

        List<String> agents = new ArrayList<>(agent.getAgents());
        Set<String> receivers = new HashSet<>();
        String agentToTip;
        while (receivers.size() < AGENTS_TO_TIP) {
            randomIndex = random.nextInt(agents.size());
            agentToTip = agents.get(randomIndex);
            receivers.add(agentToTip);
        }

        List<String> allCompanies = new ArrayList<>(agent.getStockPrices().get("0").keySet());
        Map<String, Map<String, Double>> tips = new HashMap<>();
        Map<String, Double> dailyTips;
        String company;
        for (int dayOfTheTip = newDay + 1; dayOfTheTip < newDay + TIP_DAYS; ++ dayOfTheTip) {
            if (agent.getStockPrices().get(String.valueOf(dayOfTheTip)) == null) {
                // Simulation is Ending -> There are no more tips to show
                break;
            }

            dailyTips = new HashMap<>();
            while (dailyTips.size() < NUMBER_OF_TIPS) {
                randomIndex = random.nextInt(allCompanies.size());
                company = allCompanies.get(randomIndex);
                dailyTips.put(company, agent.getStockPrices().get(String.valueOf(dayOfTheTip)).get(company));
            }
            tips.put(String.valueOf(dayOfTheTip), dailyTips);
        }

        return Utils.createOracleTipMessage(receivers, Utils.gson.toJson(tips));
    }
}
