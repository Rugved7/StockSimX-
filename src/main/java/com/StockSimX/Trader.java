package com.StockSimX;

import java.util.Random;

public class Trader implements Runnable {
    private final int id;
    private final Market market;
    private final int ticks;

    public Trader(int id, Market market, int ticks) {
        this.id = id;
        this.market = market;
        this.ticks = ticks;
    }

    @Override
    public void run() {
        Random random = new Random();
        for (int i = 1; i <= ticks ; i++) {
            try {
                Thread.sleep(random.nextInt(500));
                boolean buy = random.nextBoolean();
                int quantity = random.nextInt(10) + 1;
                int price = 100 + random.nextInt(50);

                String action = buy ? "BUY" : "SELL";
                System.out.printf("Trader %d Tick %d → %s %d shares @ ₹%d\n", id, i, action, quantity, price);
                market.executeOrder(id,action,quantity,price);
            }  catch (InterruptedException e) {
                System.out.println("Trader " + id + " interrupted");
            }
        }
    }
}
