package stockmarket.utils;

public enum ActionType {
    START_BANK        , // Start a Bank Account
    CHECK_BALANCE     , // Check the Balance for the Agent's Bank Account
    TRANSFER_MONEY    , // Transfer Money to Another Agent's Account
    START_STOCK       , // Start an Entry at the Stock Market
    CHECK_OWNED_STOCK , // Check the Agent's Owned Stock
    CHECK_STOCK_PRICES, // Check the Current Stock Prices
    BUY_STOCK         , // Buy Stock from a Company
    SELL_STOCK        , // Sell All Owned Stocks
    MANAGE_MONEY      , // Place / Remove Money from an Agent's Account (must be Stock Market)
    DAY_OVER          , // The Agent has concluded its Actions for the Day
    NEW_DAY           , // New Day has Started
    ORACLE_TIP        , // Oracle Gives a Tip to an Agent
    ASK_PERMISSION    , // Ask for Permission to Get Loans from the Other Agents
    GIVE_PERMISSION   , // Give Permission to 1 Agent to Ask for Loans
    LOAN_MONEY        , // Agent Asks for a Loan to another Agent
    FINISHED          , // Agent has Finished Operations after the Loan Contract has Ended
}
