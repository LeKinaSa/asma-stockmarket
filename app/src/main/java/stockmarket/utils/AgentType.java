package stockmarket.utils;

import java.util.Arrays;
import java.util.List;

public enum AgentType {
    BANK,
    STOCK,
    ORACLE,
    TIME,
    NORMAL;

    public List<String> getOntologies() {
        switch (this) {
            case BANK: 
                return Arrays.asList(
                    ActionType.START.toString(),
                    ActionType.CHECK_BALANCE.toString(),
                    ActionType.TRANSFER_MONEY.toString(),
                    ActionType.MANAGE_MONEY.toString()
                );
            case STOCK:
                return Arrays.asList(
                    ActionType.START.toString(),
                    ActionType.CHECK_OWNED_STOCK.toString(),
                    ActionType.CHECK_STOCK_PRICES.toString(),
                    ActionType.BUY_STOCK.toString(),
                    ActionType.NEW_DAY.toString()
                );
            case ORACLE:
                return Arrays.asList(
                    ActionType.NEW_DAY.toString()
                );
            case TIME:
                return Arrays.asList(
                    ActionType.DAY_OVER.toString()
                );
            case NORMAL:
                return Arrays.asList(
                    ActionType.NEW_DAY.toString(),
                    ActionType.ORACLE_TIP.toString(),
                    ActionType.LOAN_MONEY.toString()
                );
            default:
                return Arrays.asList();
        }
    }
}
