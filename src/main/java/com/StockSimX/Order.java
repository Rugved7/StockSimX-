package com.StockSimX;

public class Order {
    private String traderName;
    private boolean isBuy;
    private int price;
    private int quantity;

    public Order(String traderName, boolean isBuy, int price, int quantity) {
        this.traderName = traderName;
        this.isBuy = isBuy;
        this.price = price;
        this.quantity = quantity;
    }

    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    public boolean isBuy() {
        return isBuy;
    }

    public void setBuy(boolean buy) {
        isBuy = buy;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
