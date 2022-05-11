package stockmarket.behaviours.managers.messages;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.NormalAgent;
import stockmarket.behaviours.RequestInitiatorBehaviour;
import stockmarket.behaviours.managers.protocols.RequestInitiator;
import stockmarket.utils.Action;
import stockmarket.utils.ActionType;
import stockmarket.utils.MoneyTransfer;

public class NormalAgentNewDayListener extends NewDayListener {
	private final Map<String, List<MoneyTransfer>> loans = new HashMap<>();
	private final NormalAgent agent;
    private final OracleTipListener tipListener;

    public NormalAgentNewDayListener(NormalAgent agent, OracleTipListener tipListener) {
        this.agent = agent;
        this.tipListener = tipListener;
    }

    public List<MoneyTransfer> getLoansForTheDay(int day) {
        String dayString = String.valueOf(day);
        if (loans.containsKey(dayString)) {
            return loans.get(dayString);
        }
        return new ArrayList<>();
    }

    public void removeDayFromLoans(int day) {
        loans.remove(String.valueOf(day));
    }

    @Override
    public void actionOnReceive(ACLMessage message) {
        super.actionOnReceive(message);

        // Check which Stocks we possess
        // TODO
        Map<String, Double> ownedStocks = new HashMap<>(); // TODO

        // Collect Money from stocks that "end" that day and that we possess
        Map<String, Double> tipsForTheDay = tipListener.getTipsForTheDay(day);
        double nStocks;
        for (String stock : tipsForTheDay.keySet()) {
            if (ownedStocks.containsKey(stock)) {
                nStocks = ownedStocks.get(stock);
                agent.addBehaviour(new RequestInitiatorBehaviour(
                    agent, new RequestInitiator(
                        agent.getStockAgents(), new Action(ActionType.BUY_STOCK, String.valueOf(-nStocks))
                    )
                ));
            }
        }
        tipListener.removeDayFromTips(day);

        // Pay Loans that "end" that day
        List<MoneyTransfer> loansForTheDay = getLoansForTheDay(day);
        for (MoneyTransfer loan : loansForTheDay) {
            agent.addBehaviour(new RequestInitiatorBehaviour(
                agent, new RequestInitiator(
                    agent.getBankAgents(), new Action(ActionType.TRANSFER_MONEY, loan.toString())
                )
            ));
        }
        removeDayFromLoans(day);

        // TODO: check if we need this sleep
        // Utils.sleep(2); // Wait for Oracle Tips and Money Transfers

        // TODO: ask for loan
        
        // check bank balance (check that we have enough money to pay loans)
        // loan - request
            // request response
            // Yes: contract net + loans + buy stocks + day over
            // No: day over (contract net + give loan)

        // TODO: daily agent behaviour
    }

    // LOAN
    /*
        - if we want loan, we auto-deny all loans requests
        loan % needs to be smaller than stock gains
        we accept loan when loan % > stock gain %
    */
}
