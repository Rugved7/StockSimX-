package com.StockSimX;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.locks.ReentrantLock;

public class OrderBook {
    private final String symbol;
    private final ConcurrentSkipListMap<Double, Queue<Order>> buyOrders;
    private final ConcurrentSkipListMap<Double, Queue<Order>> sellOrders;
    private final ReentrantLock matchingLock;

    public OrderBook(String symbol) {
        this.symbol = symbol;
        this.buyOrders = new ConcurrentSkipListMap<>(Collections.reverseOrder());
        this.sellOrders = new ConcurrentSkipListMap<>();
        this.matchingLock = new ReentrantLock(true);
    }

    public void addOrders(Order order) {
        Map<Double,Queue<Order>> orderMap = (order.getType() == OrderType.BUY) ? buyOrders : sellOrders;

        orderMap.computeIfAbsent(order.getPrice(),
                k -> new ConcurrentLinkedQueue<>()).offer(order);
        System.out.printf("[%s] ORDER ADDED: %s%n", getCurrentTime(), order);

        synchronized (this){
            this.notifyAll();
        }
    }

//  * Thread-safe order matching algorithm -> Main part of Project
    public List<String> matchOrders() {
        matchingLock.lock();
        try {
            List<String> matches = new ArrayList<>();

//            Trade can happen iff buyPrice >= sellPrice
            while (!buyOrders.isEmpty() && !sellOrders.isEmpty()){
                Double highestBuyPrice = buyOrders.firstKey(); // highest buy price
                Double lowestSellPrice = sellOrders.firstKey(); // lowest sell price

                if(highestBuyPrice >= lowestSellPrice){
                    Queue<Order> buyQueue = buyOrders.get(highestBuyPrice);
                    Queue<Order> sellQueue = sellOrders.get(lowestSellPrice);

                    Order buyOrder = buyQueue.poll();
                    Order sellOrder = sellQueue.poll();

                    if(buyOrder != null && sellOrder != null){
                        int tradedQuantity = Math.min(buyOrder.getQuantity(),sellOrder.getQuantity());
                        double tradedPrice = sellOrder.getPrice();

                        String match = String.format(
                                "TRADE EXECUTED: %s bought %d shares from %s at $%.2f (Total: $%.2f)",
                                buyOrder.getTraderId(),
                                tradedQuantity,
                                sellOrder.getTraderId(),
                                tradedPrice,
                                tradedQuantity + tradedPrice);

                        matches.add(match);

                        // If buy order was larger, put remainder back
                        if(buyOrder.getQuantity() > tradedQuantity) {
                            Order remainingBuy = new Order(
                                    buyOrder.getTraderId(),
                                    buyOrder.getSymbol(),
                                    OrderType.BUY,
                                    buyOrder.getQuantity() - tradedQuantity,
                                    buyOrder.getPrice()
                            );
                            buyQueue.offer(remainingBuy);
                        }
                        if(sellOrder.getQuantity() > tradedQuantity) {
                            Order remainingSell = new Order(
                                    sellOrder.getTraderId(),
                                    sellOrder.getSymbol(),
                                    OrderType.SELL,
                                    sellOrder.getQuantity() - tradedQuantity,
                                    sellOrder.getPrice()
                            );
                            sellQueue.offer(remainingSell);
                        }
                    }
                    if (buyQueue.isEmpty()) {
                        buyOrders.remove(highestBuyPrice);
                    }
                    if(sellQueue.isEmpty()){
                        sellOrders.remove(lowestSellPrice);
                    }
                } else {
                    break;
                }
            }
            return matches;
        } finally {
            matchingLock.unlock();
        }
    }

    public boolean waitForOrders(long timeoutMs) {
        synchronized (this){
            if(buyOrders.isEmpty() && sellOrders.isEmpty()) {
                try {
                    this.wait(timeoutMs);
                } catch (InterruptedException Ex){
                    Thread.currentThread().interrupt();
                    return false;
                }
            }
            return !buyOrders.isEmpty() || !sellOrders.isEmpty();
        }
    }

    public String getOrderBookStatus() {
        return String.format("OrderBook[%s]: %d buy levels, %d sell levels",
                symbol, buyOrders.size(), sellOrders.size());
    }

    public String getDetailedStatus() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("=== ORDER BOOK: %s ===\n", symbol));

        // Show sell orders (lowest price first)
        sb.append("SELL ORDERS (Ask):\n");
        sellOrders.forEach((price, queue) -> {
            sb.append(String.format("  $%.2f: %d orders\n", price, queue.size()));
        });

        sb.append("--- SPREAD ---\n");

        // Show buy orders (highest price first)
        sb.append("BUY ORDERS (Bid):\n");
        buyOrders.forEach((price, queue) -> {
            sb.append(String.format("  $%.2f: %d orders\n", price, queue.size()));
        });

        return sb.toString();
    }

    private String getCurrentTime() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
    }
    public String getSymbol() {
        return symbol;
    }
}
