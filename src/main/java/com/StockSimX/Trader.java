package com.StockSimX;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicInteger;

public class Trader implements Runnable {
    private final String traderId;
    private final List<Stock> avaliableStocks;
    private final Map<String,OrderBook> orderBooks;
    private final Random random;

    private final AtomicInteger ordersPlaced;
    private final CyclicBarrier marketCyclicBarrier;
    private final CountDownLatch simulationLatch;
    private volatile boolean running;

    private final int maxOrdersPerTrader;
    private final int minTradingDelay;
    private final int maxTradingDelay;
}
