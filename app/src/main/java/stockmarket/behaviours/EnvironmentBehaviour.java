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
import stockmarket.behaviours.protocols.RequestInitiatorBehaviour;
import stockmarket.managers.protocols.Initiator;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.Utils;

public class EnvironmentBehaviour extends CyclicBehaviour {
    private final EnvironmentAgent agent;
    private int delay;
    private int agentsToTip;
    private int tipDays;
    private int numberOfTipsPerDay;

    public EnvironmentBehaviour(EnvironmentAgent agent) {
        this.agent = agent;
    }

    @Override
    public void action() {
        if (agent.getNAgents() > 0 && agent.getDayListener().canPassToNextDay()) {
            Utils.sleep(delay);
            int nextDay = agent.getDayListener().nextDay();
            Utils.space();
            Utils.info(agent, "Starting Day " + nextDay);
            if (nextDay > agent.getSimulationDays() || agent.simulationIsOver(nextDay)) {
                endSimulation();
                return;
            }
            startDay(nextDay);
        }
    }

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setAgentsToTip(int agentsToTip) {
        this.agentsToTip = agentsToTip;
    }

    public void setTipDays(int tipDays) {
        this.tipDays = tipDays;
    }

    public void setNumberOfTipsPerDay(int numberOfTipsPerDay) {
        this.numberOfTipsPerDay = numberOfTipsPerDay;
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
        Utils.info(agent, "Ending the Simulation");
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
            new EndSimulationBehaviour(agent)
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
        while (receivers.size() < agentsToTip) {
            randomIndex = random.nextInt(agents.size());
            agentToTip = agents.get(randomIndex);
            receivers.add(agentToTip);
        }

        List<String> allCompanies = new ArrayList<>(agent.getStockPrices().get("0").keySet());
        Map<String, Map<String, Double>> tips = new HashMap<>();
        Map<String, Double> dailyTips;
        String company;
        for (int dayOfTheTip = newDay + 1; dayOfTheTip < newDay + tipDays + 1; ++ dayOfTheTip) {
            if (agent.getStockPrices().get(String.valueOf(dayOfTheTip)) == null) {
                // Simulation is Ending -> There are no more tips to show
                break;
            }

            dailyTips = new HashMap<>();
            while (dailyTips.size() < numberOfTipsPerDay) {
                randomIndex = random.nextInt(allCompanies.size());
                company = allCompanies.get(randomIndex);
                dailyTips.put(company, agent.getStockPrices().get(String.valueOf(dayOfTheTip)).get(company));
            }
            tips.put(String.valueOf(dayOfTheTip), dailyTips);
        }

        return Utils.createOracleTipMessage(receivers, Utils.gson.toJson(tips));
    }
}
