package com.StockSimX;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Market {
    private final Lock lock = new ReentrantLock(true);

}
