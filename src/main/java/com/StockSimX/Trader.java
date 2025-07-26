package com.StockSimX;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class Trader implements Runnable {
    private final String traderId;
    private final List<Stock> avaliableStocks;
    private final Map<String,OrderBook> orderBooks;
    private final Random random;

    private final AtomicInteger ordersPlaced;
    private final CyclicBarrier marketCyclicBarrier;
    private final CountDownLatch simulationLatch;
    private volatile boolean running;

    private final int maxOrdersPerTrader;
    private final int minTradingDelay;
    private final int maxTradingDelay;

    public Trader(String traderId, List<Stock> stocks,
                  Map<String, OrderBook> orderBooks,
                  CyclicBarrier barrier, CountDownLatch latch) {

        this.traderId = traderId;
        this.avaliableStocks = new ArrayList<>(stocks);
        this.orderBooks = orderBooks;
        this.random = new Random();
        this.ordersPlaced = new AtomicInteger(0);
        this.marketCyclicBarrier = barrier;
        this.simulationLatch = latch;
        this.running = true;

        this.maxOrdersPerTrader = 8;
        this.minTradingDelay = 100;
        this.maxTradingDelay = 500;
    }

    @Override
    public void run() {
        System.out.printf("[TRADER START] %s begins trading%n", traderId);

        try {
            while(running && ordersPlaced.get() < maxOrdersPerTrader){
                try {
                    marketCyclicBarrier.await();
                    System.out.printf("[MARKET CYCLE] %s ready for trading%n", traderId);
                } catch (BrokenBarrierException ex){
                    System.out.printf("[ERROR] %s: Market cycle barrier broken%n", traderId);
                    break;
                }
                placeRandomOrder();

                int orderCount = ordersPlaced.incrementAndGet();
                Thread.sleep(minTradingDelay + random.nextInt(maxTradingDelay - minTradingDelay));

                if(orderCount % 3 == 0) {
                    System.out.printf("[PROGRESS] %s has placed %d orders%n",
                            traderId, orderCount);
                }
            }
        } catch (InterruptedException e) {
           Thread.currentThread().interrupt();
            System.out.printf("[INTERRUPTED] %s was interrupted%n", traderId);
        } finally {
            simulationLatch.countDown();
            System.out.printf("[TRADER COMPLETE] %s finished with %d orders%n",
                    traderId, ordersPlaced.get());
        }
    }

    public void placeRandomOrder() {
        try {
            Stock selectedStock = avaliableStocks.get(random.nextInt(avaliableStocks.size()));
            OrderType orderType = random.nextBoolean() ? OrderType.BUY : OrderType.SELL;

            int baseQuantity = (random.nextInt(10) + 1) * 100;
            int quantity = baseQuantity;

            double curentPrice = selectedStock.getCurrentPrice();
            double priceVariation = generatePriceVariation(orderType);
            double orderPrice = Math.max(0.01,curentPrice * (1+priceVariation));

            Order order = new Order(traderId,selectedStock.getSymbol(), orderType,quantity,orderPrice);

            OrderBook orderBook = orderBooks.get(selectedStock.getSymbol());
            if(orderBook != null){
                orderBook.addOrders(order);
                System.out.printf("[ORDER PLACED] %s: %s%n", traderId, order);
            } else {
                System.out.printf("[ERROR] %s: No order book for %s%n",
                        traderId, selectedStock.getSymbol());
            }
        } catch (Exception ex){
            System.out.printf("[ERROR] %s: Failed to place order - %s%n",
                    traderId, ex.getMessage());
        }
    }

    private double generatePriceVariation(OrderType orderType) {
        double baseVariation = random.nextGaussian() * 0.02;
        if(orderType == OrderType.BUY){
            return baseVariation - (random.nextDouble() * 0.01);
        } else {
            return baseVariation + (random.nextDouble() * 0.01);
        }
    }
    public void stop() {
        running = false;
        System.out.printf("[STOP SIGNAL] %s received stop signal%n", traderId);
    }

    public int getOrdersPlaced() {
        return ordersPlaced.get();
    }

    public String getTraderId() {
        return traderId;
    }

    public boolean isRunning() {
        return running;
    }

    public String getTradingStats(){
        return String.format("Trader[%s]: %d orders placed, Status: %s",
                traderId,
                ordersPlaced.get(),
                running ? "ACTIVE" : "STOPPED");
    }

    @Override
    public String toString() {
        return String.format("Trader{id='%s', orders=%d, running=%s}",
                traderId, ordersPlaced.get(), running);
    }
}
