package com.StockSimX;

import java.util.Random;

public class Trader implements Runnable {
    private final String name;
    private final Market market;
    private final Random random;

    public Trader(String name, Market market, Random random) {
        this.name = name;
        this.market = market;
        this.random = new Random();
    }

    @Override
    public void run(){
        try {
            while (true){
                Thread.sleep(500 + random.nextInt(1000));
                boolean isBuy = random.nextBoolean();
                int price = 90 + random.nextInt(21);
                int quantity = 1 + random.nextInt(10);

//                Order order = new Order(name,isBuy,price,quantity);

                System.out.println(">> " + name + " placed " + (isBuy ? "BUY" : "SELL") +
                        " order | Price: ₹" + price + " | Qty: " + quantity);

                market.executeOrders(order);
            }
        } catch (InterruptedException Ex){
            System.out.println(name + " stopped trading");
        }
    }
}
