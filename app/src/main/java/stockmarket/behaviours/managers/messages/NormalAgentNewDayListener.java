package stockmarket.behaviours.managers.messages;

import java.util.HashMap;
import java.util.Map;
import jade.lang.acl.ACLMessage;
import stockmarket.agents.NormalAgent;
import stockmarket.utils.Utils;

public class NormalAgentNewDayListener extends NewDayListener {
	private final Map<String, Map<String, Double>> loans = new HashMap<>();
	private final NormalAgent agent;
    private final OracleTipListener tipListener;

    public NormalAgentNewDayListener(NormalAgent agent, OracleTipListener tipListener) {
        this.agent = agent;
        this.tipListener = tipListener;
    }

    @Override
    public void actionOnReceive(ACLMessage message) {
        super.actionOnReceive(message);
        String dayString = String.valueOf(day);

        // collect money from stocks that "end" that day

        if (loans.containsKey(dayString)) {
            Map<String, Double> dailyLoans = loans.get(dayString);
            if (dailyLoans.size() > 0) {
                // TODO: ask bank to transfer money
            }
            loans.remove(dayString);
        }

        Utils.sleep(2); // Wait for Oracle Tips and Money Transfers

        // check bank balance (check that we have enough money to pay loans)
        // buy stocks (based on tips)
            // loan
        // day over

        // TODO: daily agent behaviour
    }

    // LOAN
    /*
        - if we want loan, we auto-deny all loans requests
        loan % needs to be smaller than stock gains
        we accept loan when loan % > stock gain %
    */
}
