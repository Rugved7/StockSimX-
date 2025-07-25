package com.StockSimX;

import java.util.LinkedList;
import java.util.Queue;

// OrderBook maintains the queue of all stock orders (buy/sell) submitted by Traders.
public class OrderBook {
    private final Queue<Order> orders = new LinkedList<>();

    // Add a new order to the queue
    public synchronized void addOrder(Order order) {
        orders.add(order);
        notify(); // Notify one waiting trader thread that an order is available
    }

    // Fetch and remove an order from the queue
    public synchronized Order getNextOrder() {
        while (orders.isEmpty()) {
            try {
                wait(); // If no orders, wait for someone to add
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Re-set the interrupt status
                return null;
            }
        }
        return orders.poll(); // Return and remove the head of the queue
    }

    // Optional: to check how many orders are pending
    public synchronized int size() {
        return orders.size();
    }
}
