package com.StockSimX;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class MatchingEngine implements Runnable{
    private final Map<String, OrderBook> orderBooks;
    private final Map<String, Stock> stocks;

    private final AtomicBoolean running;
    private final AtomicLong totalMatches;
    private final AtomicLong totalVolumeTraded;

    private final ExecutorService backgroundExecutor;

    private final int matchingIntervalMs;
    private final int maxWaitTimeMs;

    private final Object matchingSignal = new Object();

    public MatchingEngine(Map<String, OrderBook> orderBooks,  Map<String, Stock> stocks) {
        this.orderBooks = new HashMap<>(orderBooks);
        this.stocks = new HashMap<>(stocks);
        this.running = new AtomicBoolean(false);
        this.totalMatches = new AtomicLong(0);
        this.totalVolumeTraded = new AtomicLong(0);

        this.backgroundExecutor = Executors.newSingleThreadExecutor(r -> {
            Thread t = new Thread(r,"MatchingEngine-Background");
            t.setDaemon(true);
            return t;
        });

        this.matchingIntervalMs = 200;
        this.maxWaitTimeMs = 1000;

        System.out.println("[MATCHING ENGINE] Initialized for " + orderBooks.size() + " stocks");
    }
    public void start() {
        if(running.compareAndSet(false,true)){
            System.out.println("[MATCHING ENGINE] Starting order matching");

            synchronized (matchingSignal){
                matchingSignal.notifyAll();
            }
        }
    }

    public void stop() {
        if(running.compareAndSet(true,false)){
            System.out.println("[MATCHING ENGINE] Stopping order matching");

            synchronized (matchingSignal) {
                matchingSignal.notifyAll();
            }
            backgroundExecutor.shutdown();
            try {
                if(!backgroundExecutor.awaitTermination(5, TimeUnit.SECONDS)){
                    backgroundExecutor.shutdown();
                }
            } catch (InterruptedException ex){
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public void run() {
        System.out.println("[MATCHING ENGINE] Engine thread started");

        try {
            waitForStart();

            while(running.get()){
                try {
                    processAllOrderBooks();
                    Thread.sleep(matchingIntervalMs);
                } catch (InterruptedException ex){
                    Thread.currentThread().interrupt();
                    System.out.println("[MATCHING ENGINE] Engine interrupted");
                    break;
                }
            }
        } catch (InterruptedException ex){
            System.out.printf("[ERROR] Matching engine error: %s%n", ex.getMessage());
        } finally {
            System.out.println("[MATCHING ENGINE] Engine thread stopped");
        }
    }

    private void waitForStart() throws InterruptedException {
        synchronized (matchingSignal){
            while (!running.get()){
                System.out.println("[MATCHING ENGINE] Waiting for start signal...");
                matchingSignal.wait();
            }
        }
        System.out.println("[MATCHING ENGINE] Start signal received, beginning matching");
    }

    private void processAllOrderBooks() {
        List<CompletableFuture<Void>> matchingTasks = new ArrayList<>();

        for(Map.Entry<String,OrderBook> entry : orderBooks.entrySet()) {
            String symbol = entry.getKey();
            OrderBook orderBook = entry.getValue();

            CompletableFuture<Void> matchingTask = CompletableFuture
                    .supplyAsync(() -> processOrderBook(symbol,orderBook),backgroundExecutor)
                    .thenAccept(matches -> handleMatches(symbol,matches))
                    .exceptionally(throwable -> {
                        System.out.printf("[ERROR] Matching failed for %s: %s%n",
                                symbol, throwable.getMessage());
                        return null;
                    });
            matchingTasks.add(matchingTask);
        }

        CompletableFuture<Void> allMatches = CompletableFuture.allOf(
                matchingTasks.toArray(new CompletableFuture[0]));

        try{
            allMatches.get(maxWaitTimeMs,TimeUnit.MILLISECONDS);
        } catch (TimeoutException e){
            System.out.println("[WARNING] Matching cycle took longer than expected");
        } catch (Exception e) {
            System.out.printf("[ERROR] Error in matching cycle: %s%n", e.getMessage());
        }
    }

    private List<String> processOrderBook(String symbol, OrderBook orderBook){
        if(!orderBook.waitForOrders(100)){
            return Collections.emptyList();
        }

        List<String> matches = orderBook.matchOrders();

        if(!matches.isEmpty()){
            System.out.printf("[MATCHING] %s: Found %d matches%n", symbol, matches.size());
        }
        return matches;
    }

    private void handleMatches(String symbol, List<String> matches){
        if(matches.isEmpty()) return ;

        totalMatches.addAndGet(matches.size());

        for(String match : matches){
            System.out.printf("[TRADE COMPLETE] %s%n", match);

            long volume = extractVolumeFromMatch(match);
            totalVolumeTraded.addAndGet(volume);

            Stock stock = stocks.get(symbol);
            if(stock != null){
                stock.addVolume(volume);
            }
        }

        CompletableFuture.runAsync(() -> logMatchDetails(symbol,matches),backgroundExecutor);
    }
    private long extractVolumeFromMatch(String match) {
        try {
            String[] parts = match.split(" ");
            for(int i=0;i < parts.length-1;i++){
                if("bought".equals(parts[i]) && "shares".equals(parts[i+2])){
                    return Long.parseLong(parts[i+1]);
                }
            }
        } catch (Exception e) {
        }
        return 100;
    }

    private void logMatchDetails(String symbol, List<String> matches){
        System.out.printf("[MATCH REPORT] %s: Processed %d matches%n",
                symbol, matches.size());
    }

    public void requestMatching() {
        synchronized (matchingSignal){
            matchingSignal.notify();
        }
    }

    public String getStatistics() {
        return String.format("MatchingEngine Stats: %d total matches, %d shares traded",
                totalMatches.get(), totalVolumeTraded.get());
    }

    public boolean isRunning() {
        return running.get();
    }

    public long getTotalMatches(){
        return totalMatches.get();
    }

    public long getTotalVolumeTraded() {
        return totalVolumeTraded.get();
    }
}
