package stockmarket.utils;

public enum ActionType {
    START             , // Start a Bank Account or an Entry at the Stock Market
    CHECK_BALANCE     , // Check the Balance for the Agent's Bank Account
    TRANSFER_MONEY    , // Transfer Money to Another Agent's Account
    CHECK_OWNED_STOCK , // Check the Agent's Owned Stock
    CHECK_STOCK_PRICES, // Check the Current Stock Prices
    BUY_STOCK         , // Buy / Sell Stock
    MANAGE_MONEY      , // Place / Remove Money from an Agent's Account (must be Stock Market)
    DAY_OVER          , // The Agent has concluded its Actions for the Day
    NEW_DAY           , // New Day has Started
    ORACLE_TIP        , // Oracle Gives a Tip to an Agent
    LOAN_MONEY        , // Agent Asks for a Loan to another Agent
}
