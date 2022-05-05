import pandas as pd
from datetime import date
import json

# https://www.kaggle.com/datasets/camnugent/sandp500?select=all_stocks_5yr.csv

start = date(2013, 2, 8)

s_and_p = pd.read_csv("data/all_stocks_5yr.csv")
s_and_p = s_and_p.drop(columns=["open", "high", "low", "volume"])

# Only keep stock that is always here from beginning to the end
s_and_p = s_and_p.groupby("Name").filter(lambda x: len(x) == 1259)

result = {}

for i, (day, dataframe) in enumerate(s_and_p.sort_values("date").groupby("date")):
    dataframe = dataframe.drop(columns="date")
    stock_prices = {item["Name"] : item["close"] for item in dataframe.to_dict("records")}
    
    result[i] = stock_prices

with open("data/formatted_stock_prices.json", "w") as f:
    json.dump(result, f)