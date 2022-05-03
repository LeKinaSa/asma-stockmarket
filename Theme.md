
# Stock Market

- agent's objective: max winnings
- the agents can buy and sell stock from companies
  - stock prices don't depend on agent stock trading behavior
- agents can loan / borrow from other agents (negociation)
- agents can receive tips on stock behavior ahead of time
  - true tips given at random by an oracle agent
- agents must respect loan terms

## Agent Types

- bank (keep track of how much money everyone has)
- stock market exchange (keeps track of who owns which stocks)
- oracle (gives tips at random time to random agents)
- normal agent (wants to maximize winnings)
- time manager (manages the days, skips to the next day)

## Interactions and Protocols

- agent can go to bank and ask how much money it has
  - request protocol
- agent can transfer money from its account to another agent's account
  - request protocol
- agent can ask the stock market what stocks it owns
  - request protocol
- agent can ask the stock market what are the current stock prices
  - request protocol
- stock market can manage bank accounts (adding and removing money from agents)
  - request protocol
- agent can buy and sell stocks
  - request protocol
- oracle can send tips about future stock prices to agents arbitrarily
  - inform message
- agents can ask other agents for loans at an interest
  - contractnet protocol
- normal agents can inform the time manager that are finished for the day
  - inform message
- the time manager can inform the normal agents and the stock market that a new day started
  - inform message

## Goals and Strategies

### Goals

- Individual goal
  - Maximize the winnings

### Strategies

- normal agent
  - using oracle's information
    - invest in stocks
    - borrow money
    - lend money

## Independent Variables (Variations introduced in Different Executions)

- Stock Price History
- Starting Money
- Oracle's Tips Distribution

## Dependent Variables (What we want to measure and evaluate)

- Agent Assets (Winnings)
