package com.StockSimX;

import java.util.*;
import java.util.concurrent.locks.*;

public class Market {
    private final Map<String, Integer> stockQuantities = new HashMap<>();
    private final Map<String, Double> stockPrices = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public void registerStock(String symbol, double price) {
        lock.writeLock().lock();
        try {
            stockQuantities.put(symbol, 1000); // Initial quantity
            stockPrices.put(symbol, price);
            System.out.println("Registered stock: " + symbol + " @ ₹" + price);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void placeOrder(String trader, Order order) {
        lock.writeLock().lock();
        try {
            String stock = order.getStockSymbol();
            int quantity = order.getQuantity();

            if (!stockQuantities.containsKey(stock)) {
                System.out.println(trader + " tried trading non-listed stock: " + stock);
                return;
            }

            if (order.getType() == Order.OrderType.BUY) {
                stockQuantities.put(stock, stockQuantities.get(stock) + quantity);
                stockPrices.put(stock, stockPrices.get(stock) * 1.01); // +1%
                System.out.println(trader + " bought " + quantity + " shares of " + stock);
            } else {
                if (stockQuantities.get(stock) < quantity) {
                    System.out.println(trader + " failed to sell (not enough shares): " + stock);
                    return;
                }
                stockQuantities.put(stock, stockQuantities.get(stock) - quantity);
                stockPrices.put(stock, stockPrices.get(stock) * 0.99); // -1%
                System.out.println(trader + " sold " + quantity + " shares of " + stock);
            }

            System.out.println("Market Update [" + stock + "]: ₹" + String.format("%.2f", stockPrices.get(stock)) +
                    ", Qty: " + stockQuantities.get(stock));

        } finally {
            lock.writeLock().unlock();
        }
    }
}
