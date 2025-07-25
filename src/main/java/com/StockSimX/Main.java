package com.StockSimX;

import java.util.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Market market = new Market();

        // Register stocks
        market.registerStock("AAPL", 150.0);
        market.registerStock("GOOG", 2800.0);
        market.registerStock("TSLA", 700.0);

        // Start traders
        List<Trader> traders = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Trader trader = new Trader("Trader-" + i, market);
            trader.start();
            traders.add(trader);
        }

        // Let simulation run for 10 seconds
        Thread.sleep(10_000);

        // Stop all traders
        for (Trader t : traders) {
            t.shutdown();
        }

        // Wait for all threads to finish
        for (Trader t : traders) {
            t.join();
        }

        System.out.println("\n=== Simulation ended ===");
    }
}
