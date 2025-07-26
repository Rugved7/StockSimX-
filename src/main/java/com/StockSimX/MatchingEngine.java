package com.StockSimX;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

public class MatchingEngine implements Runnable{
    private final Map<String, OrderBook> orderBooks;
    private final Map<String, Stock> stocks;

    private final AtomicBoolean running;
    private final AtomicLong totalMatches;
    private final AtomicLong totalVolumeTraded;

    private final ExecutorService backgroundExecutor;

    private final int matchingIntervalMs;
    private final int maxWaitTimeMs;

    private final Object matchingSignal = new Object();
}
