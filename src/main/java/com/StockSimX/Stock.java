package com.StockSimX;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class Stock {
    private final String symbol;
    private volatile double currentPrice;
    private final AtomicLong totalVolume;
    private final ReadWriteLock priceLock;
    private final Random random;

    public Stock(String symbol, double initialPrice){
        this.symbol = symbol;
        this.currentPrice = initialPrice;
        this.totalVolume = new AtomicLong(0);
        this.priceLock = new ReentrantReadWriteLock(false);
        this.random = new Random();
    }

    public double getCurrentPrice() {
        priceLock.readLock().lock();
        try {
            return currentPrice;
        } finally {
            priceLock.readLock().unlock();
        }
    }

    public void updatePrice(double newPrice) {
        priceLock.writeLock().lock();
        try {
            this.currentPrice = Math.max(0.01,newPrice);
            System.out.printf("[PRICE UPDATE] %s: $%.2f%n", symbol, this.currentPrice);
        } finally {
            priceLock.writeLock().unlock();
        }
    }

    public void stimulateFluctuation() {
        double changePercent = random.nextGaussian() * 0.02; // 2% movement
        double currentPrice = getCurrentPrice();
        double newPrice = currentPrice * (1 + changePercent);

        updatePrice(newPrice);
    }

}
