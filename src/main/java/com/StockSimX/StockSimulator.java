package com.StockSimX;

import java.util.*;
import java.util.concurrent.*;


public class StockSimulator {

    // Simulation configuration
    private static final int NUM_TRADERS = 6;
    private static final int NUM_STOCKS = 4;
    private static final int SIMULATION_DURATION_SEC = 30;
    private static final int PRICE_UPDATE_INTERVAL_MS = 500;

    // Core components
    private final Map<String, Stock> stocks;
    private final Map<String, OrderBook> orderBooks;
    private final List<Trader> traders;
    private final MatchingEngine matchingEngine;

    // Thread management
    private final ExecutorService traderExecutor;
    private final ScheduledExecutorService priceUpdater;
    private final CountDownLatch simulationComplete;
    private final CyclicBarrier marketCycleBarrier;


    public StockSimulator() {
        System.out.println("=== INITIALIZING StockSimX -> A STOCK MARKET SIMULATOR ===");

        // Initialize core data structures
        this.stocks = new ConcurrentHashMap<>();
        this.orderBooks = new ConcurrentHashMap<>();
        this.traders = new ArrayList<>();

        // Initialize thread coordination objects
        this.simulationComplete = new CountDownLatch(NUM_TRADERS);
        this.marketCycleBarrier = new CyclicBarrier(NUM_TRADERS, () -> {

            System.out.println("[MARKET CYCLE] All traders synchronized - market cycle begins!");
        });

        this.traderExecutor = Executors.newFixedThreadPool(NUM_TRADERS, r -> {
            Thread t = new Thread(r);
            t.setName("Trader-" + t.getId());
            return t;
        });

        this.priceUpdater = Executors.newScheduledThreadPool(2, r -> {
            Thread t = new Thread(r);
            t.setName("PriceUpdater-" + t.getId());
            t.setDaemon(true);
            return t;
        });

        // Initialize market components
        initializeStocks();
        initializeOrderBooks();
        initializeTraders();

        // Create matching engine
        this.matchingEngine = new MatchingEngine(orderBooks, stocks);

        System.out.println("=== INITIALIZATION COMPLETE ===\\n");
    }

    private void initializeStocks() {
        System.out.println("Initializing stocks...");


        String[] stockSymbols = {"AAPL", "GOOGL", "TSLA", "MSFT"};
        double[] startingPrices = {150.0, 2800.0, 250.0, 300.0};

        for (int i = 0; i < NUM_STOCKS; i++) {
            Stock stock = new Stock(stockSymbols[i], startingPrices[i]);
            stocks.put(stockSymbols[i], stock);
            System.out.printf("  Created %s at $%.2f%n", stockSymbols[i], startingPrices[i]);
        }
    }

    private void initializeOrderBooks() {
        System.out.println("Initializing order books...");

        for (String symbol : stocks.keySet()) {
            OrderBook orderBook = new OrderBook(symbol);
            orderBooks.put(symbol, orderBook);
            System.out.printf("  Created order book for %s%n", symbol);
        }
    }

    private void initializeTraders() {
        System.out.println("Initializing traders...");

        List<Stock> stockList = new ArrayList<>(stocks.values());

        for (int i = 1; i <= NUM_TRADERS; i++) {
            String traderId = "Trader-" + i;
            Trader trader = new Trader(traderId, stockList, orderBooks,
                    marketCycleBarrier, simulationComplete);
            traders.add(trader);
            System.out.printf("  Created %s%n", traderId);
        }
    }


    public void runSimulation() {
        System.out.println("\\n=== STARTING SIMULATION ===");

        try {

            startBackgroundServices();

            startTraders();

            startMatchingEngine();

            runSimulationLoop();

            waitForCompletion();

        } catch (Exception e) {
            System.err.printf("Simulation error: %s%n", e.getMessage());
        } finally {
            cleanup();
        }

        printFinalResults();
    }


    private void startBackgroundServices() {
        System.out.println("Starting background services...");

        priceUpdater.scheduleAtFixedRate(() -> {
            for (Stock stock : stocks.values()) {
                stock.stimulatePriceFluctuation();
            }
        }, 1000, PRICE_UPDATE_INTERVAL_MS, TimeUnit.MILLISECONDS);

        priceUpdater.scheduleAtFixedRate(() -> {
            System.out.println("\\n" + generateStatusReport());
        }, 5000, 5000, TimeUnit.MILLISECONDS);

        System.out.println("Background services started");
    }

    private void startTraders() {
        System.out.println("Starting trader threads...");

        for (Trader trader : traders) {
            traderExecutor.submit(trader);
        }

        System.out.printf("Started %d trader threads%n", traders.size());
    }

    private void startMatchingEngine() {
        System.out.println("Starting matching engine...");

        Thread engineThread = new Thread(matchingEngine, "MatchingEngine-Main");
        engineThread.start();

        matchingEngine.start();

        System.out.println("Matching engine started");
    }

    private void runSimulationLoop() throws InterruptedException {
        System.out.printf("Running simulation for %d seconds...%n", SIMULATION_DURATION_SEC);

        long startTime = System.currentTimeMillis();
        long endTime = startTime + (SIMULATION_DURATION_SEC * 1000L);

        while (System.currentTimeMillis() < endTime) {
            // Check if all traders finished early
            if (simulationComplete.getCount() == 0) {
                System.out.println("All traders finished early!");
                break;
            }

            // Sleep for a bit before checking again
            Thread.sleep(1000);

            // Show remaining time
            long remaining = (endTime - System.currentTimeMillis()) / 1000;
            if (remaining % 10 == 0 && remaining > 0) {
                System.out.printf("[TIME] %d seconds remaining%n", remaining);
            }
        }
    }


    private void waitForCompletion() throws InterruptedException {
        System.out.println("Waiting for all traders to complete...");

        boolean completed = simulationComplete.await(10, TimeUnit.SECONDS);

        if (completed) {
            System.out.println("All traders completed successfully!");
        } else {
            System.out.println("Timeout waiting for traders - forcing shutdown");
            // Stop any remaining traders
            traders.forEach(Trader::stop);
        }
    }

    private void cleanup() {
        System.out.println("\\nCleaning up resources...");

        // Stop matching engine
        matchingEngine.stop();

        // Shutdown thread pools
        traderExecutor.shutdown();
        priceUpdater.shutdown();

        try {
            // Wait for threads to finish
            if (!traderExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                traderExecutor.shutdownNow();
            }
            if (!priceUpdater.awaitTermination(5, TimeUnit.SECONDS)) {
                priceUpdater.shutdownNow();
            }
        } catch (InterruptedException e) {
            traderExecutor.shutdownNow();
            priceUpdater.shutdownNow();
            Thread.currentThread().interrupt();
        }

        System.out.println("Cleanup complete");
    }


    private String generateStatusReport() {
        StringBuilder report = new StringBuilder();
        report.append("=== SIMULATION STATUS ===\\n");

        // Stock prices and volumes
        report.append("STOCK PRICES:\\n");
        for (Stock stock : stocks.values()) {
            report.append(String.format("  %s%n", stock.getStatusReport()));
        }

        // Trader statistics
        report.append("\\nTRADER STATUS:\\n");
        for (Trader trader : traders) {
            report.append(String.format("  %s%n", trader.getTradingStats()));
        }

        // Matching engine statistics
        report.append("\\nMATCHING ENGINE:\\n");
        report.append(String.format("  %s%n", matchingEngine.getStatistics()));

        return report.toString();
    }

    private void printFinalResults() {
        System.out.println("\\n=== FINAL SIMULATION RESULTS ===");

        // Final stock prices
        System.out.println("\\nFINAL STOCK PRICES:");
        for (Stock stock : stocks.values()) {
            System.out.printf("  %s%n", stock.getStatusReport());
        }

        // Trading statistics
        System.out.println("\\nTRADING STATISTICS:");
        int totalOrders = traders.stream().mapToInt(Trader::getOrdersPlaced).sum();
        System.out.printf("  Total Orders Placed: %d%n", totalOrders);
        System.out.printf("  Total Matches: %d%n", matchingEngine.getTotalMatches());
        System.out.printf("  Total Volume Traded: %d shares%n", matchingEngine.getTotalVolumeTraded());

        // Order book status
        System.out.println("\\nFINAL ORDER BOOK STATUS:");
        for (OrderBook orderBook : orderBooks.values()) {
            System.out.printf("  %s%n", orderBook.getOrderBookStatus());
        }

        System.out.println("\\n=== SIMULATION COMPLETE ===");
    }


    public static void main(String[] args) {
        System.out.println("StockSimX - Multithreaded Stock Market Simulator");
        System.out.println("=".repeat(50));

        try {
            // Create and run simulation
            StockSimulator simulator = new StockSimulator();
            simulator.runSimulation();

        } catch (Exception e) {
            System.err.printf("Fatal error: %s%n", e.getMessage());
            e.printStackTrace();
        }

        System.out.println("\\nSimulation terminated. Goodbye!");
    }
}