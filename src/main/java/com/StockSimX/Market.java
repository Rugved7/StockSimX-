package com.StockSimX;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Market {
    private final Map<String, Double> stockPrices = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public Market() {
        // Initialize with some sample stocks and prices
        stockPrices.put("AAPL", 175.50);
        stockPrices.put("GOOG", 2800.25);
        stockPrices.put("TSLA", 690.10);
        stockPrices.put("AMZN", 3400.00);
    }

    public double getPrice(String stock) {
        lock.readLock().lock();
        try {
            return stockPrices.getOrDefault(stock, -1.0);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void updatePrice(String stock, double newPrice) {
        lock.writeLock().lock();
        try {
            stockPrices.put(stock, newPrice);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Map<String, Double> getAllPrices() {
        lock.readLock().lock();
        try {
            return new HashMap<>(stockPrices);
        } finally {
            lock.readLock().unlock();
        }
    }
}
