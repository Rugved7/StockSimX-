package com.StockSimX;

import java.util.PriorityQueue;

public class OrderBook {
    private PriorityQueue<Order> buyOrders;
    private PriorityQueue<Order> sellOrders;

    public OrderBook(){
        buyOrders = new PriorityQueue<>(((o1, o2) -> o2.getPrice() - o1.getPrice() ));
        sellOrders = new PriorityQueue<>(((o1, o2) -> o1.getPrice() - o2.getPrice() ));
    }

    public void addOrder(Order order){
        if(order.isBuy()){
            buyOrders.offer(order);
        } else {
            sellOrders.offer(order);
        }
        matchOrders();
    }
    private void matchOrders(){
        while (!buyOrders.isEmpty() && !sellOrders.isEmpty()){
            Order buy = buyOrders.peek();
            Order sell = sellOrders.peek();

            // Trade can happen if buyer's price >= seller's price
            if(buy.getPrice() >= sell.getPrice()){
                int tradedQuantity = Math.min(buy.getQuantity(),sell.getQuantity());
                int tradePrice = sell.getPrice();

                System.out.println("Trade Executed: " + tradedQuantity + " units at price " + tradePrice
                        + " between Buyer [" + buy.getTraderName() + "] and Seller [" + sell.getTraderName() + "]");

                // Update remaining quantities after trade
                buy.setQuantity(buy.getQuantity() - tradedQuantity);
                sell.setQuantity(sell.getQuantity() - tradedQuantity);

                if(buy.getQuantity() == 0){
                    buyOrders.poll();
                }
                if(sell.getQuantity() == 0){
                    sellOrders.poll();
                }
            } else {
                break;
            }
        }
    }
}
