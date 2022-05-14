package stockmarket.utils;

import java.util.Arrays;
import java.util.List;

public enum AgentType {
    ENVIRONMENT,
    NORMAL;

    public List<String> getOntologies() {
        switch (this) {
            case ENVIRONMENT:
                return Arrays.asList(
                    ActionType.START_BANK.toString(),
                    ActionType.CHECK_BALANCE.toString(),
                    ActionType.TRANSFER_MONEY.toString(),
                    ActionType.CHECK_STOCK_PRICES.toString(),
                    ActionType.BUY_STOCK.toString(),
                    ActionType.SELL_STOCK.toString(),
                    ActionType.DAY_OVER.toString(),
                    ActionType.ASK_PERMISSION.toString()
                );
            case NORMAL:
                return Arrays.asList(
                    ActionType.NEW_DAY.toString(),
                    ActionType.ORACLE_TIP.toString(),
                    ActionType.GIVE_PERMISSION.toString(),
                    ActionType.LOAN_MONEY.toString(),
                    ActionType.FINISHED.toString(),
                    ActionType.END_SIMULATION.toString()
                );
            default:
                return Arrays.asList();
        }
    }
}
