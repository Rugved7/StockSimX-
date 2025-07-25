package com.StockSimX;

public class Trader extends Thread {
    private final String name;
    private final Market market;
    private volatile boolean running = true;

    public Trader(String name, Market market) {
        this.name = name;
        this.market = market;
    }

    @Override
    public void run() {
        while (running) {
            try {
                Order.OrderType type = Math.random() < 0.5 ? Order.OrderType.BUY : Order.OrderType.SELL;
                String[] stocks = {"AAPL", "GOOG", "TSLA"};
                String stock = stocks[(int) (Math.random() * stocks.length)];
                int quantity = (int) (Math.random() * 10) + 1;

                Order order = new Order(type, stock, quantity);
                market.placeOrder(name, order);

                Thread.sleep((int) (Math.random() * 1500) + 500); // 0.5 – 2 sec
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }

        System.out.println(name + " stopped.");
    }

    public void shutdown() {
        running = false;
    }
}
