package com.StockSimX;

import java.util.Objects;

public class Order {
    // All fields are final to make this class immutable and thread-safe
    private final String traderId;      // Which trader placed this order
    private final String symbol;        // Stock symbol (e.g., "AAPL", "GOOGL")
    private final OrderType type;       // BUY or SELL
    private final int quantity;         // Number of shares
    private final double price;         // Price per share
    private final long timestamp;       // When order was created (for ordering)

    public Order(String traderId, String symbol, OrderType type, int quantity, double price) {
        this.traderId = traderId;
        this.symbol = symbol;
        this.type = type;
        this.quantity = quantity;
        this.price = price;
        // Timestamp helps with order priority (first-come-first-served for same price)
        this.timestamp = System.currentTimeMillis();
    }

    // Getter methods - no setters because order is immutable
    public String getTraderId() {
        return traderId;
    }

    public String getSymbol() {
        return symbol;
    }

    public OrderType getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return String.format("%s order: %s wants to %s %d shares of %s at $%.2f",
                type,
                traderId,
                type.toString().toLowerCase(),
                quantity,
                symbol,
                price);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Order order = (Order) obj;
        return quantity == order.quantity &&
                Double.compare(order.price, price) == 0 &&
                timestamp == order.timestamp &&
                traderId.equals(order.traderId) &&
                symbol.equals(order.symbol) &&
                type == order.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(traderId, symbol, type, quantity, price, timestamp);
    }
}