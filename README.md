# ASMA Project 1

| Group 02 |  |
| --- | --- |
| Clara Martins | up201806528 |
| Daniel Monteiro | up201806185 |

## How to run

- Experience 1 : `make test1`
- Experience 2 : `make test2`
- Experience 3 : `make test3`
- Experience 4 : `make test4`
- Experience 5 : `make test5`
- Full Simulation : `make`
- Custom Execution :
  - `make clean`
  - `make compile`
  - `java -cp "app/lib/gson.jar:app/lib/jade.jar:bin" jade.Boot -agents {agents} -gui`
    - agents:
      - `environment:stockmarket.agents.EnvironmentAgent(<n_normal_agents: int (> 1)>, <simulation_days: int>, <n_agents_to_tip: int>, <n_days_to_tip: int>, <n_tips_per_day: int>, <delay: int>, <log: 0(false)/1(true)>)`, only `<n_normal_agents>` is required, the other arguments are optional
      - `aN:stockmarket.agents.NormalAgent(<extra_interest: double>, <initial_money: double>, <log: 0(false)/1(true)>)`, all arguments are optional
