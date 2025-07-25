package com.StockSimX   ;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        System.out.println("=====================================");
        System.out.println("🚀 Welcome to StockSimX");
        System.out.println("Your CLI-based Multithreaded Stock Market Simulator");
        System.out.println("=====================================\n");

        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter number of traders to simulate: ");
        int traderCount = scanner.nextInt();

        System.out.print("Enter number of ticks (market cycles): ");
        int tickCount = scanner.nextInt();

        System.out.println("\nInitializing market with " + traderCount + " traders for " + tickCount + " ticks...");
        System.out.println("Simulation will begin shortly...\n");

        System.out.println("✅ CLI setup complete. Moving to Phase 2...");

        scanner.close();
    }
}
