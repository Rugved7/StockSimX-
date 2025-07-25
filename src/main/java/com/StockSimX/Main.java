package com.StockSimX;

import com.StockSimX.Market;
import com.StockSimX.Trader;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    public static void main(String[] args) {
        System.out.println("🚀 Welcome to StockSimX - Multithreaded Stock Market");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter number of traders to simulate: ");
        int traderCount = scanner.nextInt();

        System.out.print("Enter number of ticks (market cycles): ");
        int tickCount = scanner.nextInt();

        Market market = new Market();

        ExecutorService executor = Executors.newFixedThreadPool(traderCount);
        List<Trader> traders = new ArrayList<>();

        for (int i = 1; i <= traderCount; i++) {
            Trader trader = new Trader(i, market, tickCount);
            traders.add(trader);
            executor.submit(trader);
        }

        executor.shutdown();
        System.out.println("\n🟢 Traders started. Waiting for simulation to complete...\n");

        scanner.close();
    }
}
