package com.StockSimX;

import java.util.Objects;

public class Order {
    private final int traderId;
    private final String symbol; // "AAPL", ""GOGL", etc
    private final OrderType type; // Buy || Sell
    private final int quantity;
    private final double price;
    private final long timeStamp;

    public Order(double price, int quantity, OrderType type, String symbol, int traderId) {
        this.price = price;
        this.quantity = quantity;
        this.type = type;
        this.symbol = symbol;
        this.traderId = traderId;
        this.timeStamp = System.currentTimeMillis();
    }

    public int getTraderId() {
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

    public long getTimeStamp() {
        return timeStamp;
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
    public boolean equals(Object obj){
        if(this == obj) return true;
        if(obj == null || getClass() != obj.getClass()) return false;

        Order order = (Order) obj;
        return quantity == order.quantity &&
                Double.compare(order.price, price) == 0 &&
                timeStamp == order.timeStamp &&
                Objects.equals(traderId, order.traderId) &&
                symbol.equals(order.symbol) &&
                type == order.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(traderId, symbol, type, quantity, price, timeStamp);
    }
}

