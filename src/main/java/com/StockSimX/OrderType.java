package com.StockSimX;

public enum OrderType {
    BUY,
    SELL;

    public OrderType opposite(){
        return this == BUY ? SELL : BUY;
    }

    @Override
    public String toString() {
        return this == BUY ? "Buy" : "Sell";
    }
}
