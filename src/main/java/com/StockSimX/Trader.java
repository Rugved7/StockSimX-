package com.StockSimX;

public class Trader implements Runnable {
    private final int id;
    private final Market market;
    private final int ticks;

    public Trader(int id, Market market, int ticks) {
        this.id = id;
        this.market = market;
        this.ticks = ticks;
    }

    @Override
    public void run() {

    }
}
