package com.StockSimX;

public class Order {
    public enum OrderType { BUY, SELL }

    private final OrderType type;
    private final String stockSymbol;
    private final int quantity;

    public Order(OrderType type, String stockSymbol, int quantity) {
        this.type = type;
        this.stockSymbol = stockSymbol;
        this.quantity = quantity;
    }

    public OrderType getType() {
        return type;
    }

    public String getStockSymbol() {
        return stockSymbol;
    }

    public int getQuantity() {
        return quantity;
    }
}
