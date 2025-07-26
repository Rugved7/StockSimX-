## 📈 StockSimX - Multithreaded Stock Market Simulator
A comprehensive Java application demonstrating advanced concurrency concepts through real-time stock exchange simulation

 ## 🎯 Overview
StockSimX simulates a real-time stock exchange where multiple traders (threads) place buy/sell orders simultaneously while a central matching engine processes trades safely.
This project demonstrates advanced Java Multithreading concepts used in actual financial systems.

## Architecture Diagram

                ┌──────────────────────────────┐
                │        Trader Thread-1       │
                └────────────┬─────────────────┘
                             │
                ┌────────────▼────────────┐
                │        Trader Thread-2  │
                └────────────┬────────────┘
                             │
                ┌────────────▼────────────┐
                │        Trader Thread-N  │
                └────────────┬────────────┘
                             │
                             ▼
                ┌──────────────────────────────┐
                │   Shared OrderBooks (AAPL,   │
                │   GOOGL, TSLA, MSFT, etc.)   │
                └────────────┬─────────────────┘
                             │
                             ▼
                ┌──────────────────────────────┐
                │       Matching Engine        │
                │  (Matches Buy/Sell Orders)   │
                └──────────────────────────────┘


## 🔧 Multithreading Concepts Used

| **Concept/Implementation** | **Class/File**           | **Purpose**                          |
|----------------------------|--------------------------|--------------------------------------|
| `Thread` / `Runnable`      | `Trader.java`            | Individual trader behavior           |
| `ExecutorService`          | `StockSimulator.java`    | Thread pool management               |
| `ReadWriteLock`            | `Stock.java`             | Concurrent price updates             |
| `ReentrantLock`            | `OrderBook.java`         | Fair order matching                  |
| `CyclicBarrier`            | `Trader.java`            | Synchronize trading cycles           |
| `CountDownLatch`           | `StockSimulator.java`    | Coordination completion              |
| `AtomicInteger` / `Long`   | Throughout               | Thread-safe counters                 |
| `volatile` variables       | `Stock.java`, `Trader.java` | Memory visibility                 |
| `wait()` / `notify()`      | `OrderBook.java`, `MatchingEngine.java` | Thread communication  |
| `CompletableFuture`        | `MatchingEngine.java`    | Asynchronous processing              |
| Concurrent Collections     | `OrderBook.java`         | Thread-safe data structures          |



## 🚀 Quick Start

Prerequisites

Java 17 or higher

Maven 3.8+

IntelliJ IDEA (recommended)


## 🏗️ Project Structure

```
src/main/java/
├── 📄 StockSimulator.java      # Main orchestrator & entry point
├── 📄 MatchingEngine.java      # Central order processing engine
├── 📄 Trader.java              # Individual trader threads
├── 📄 OrderBook.java           # Thread-safe order management
├── 📄 Stock.java               # Thread-safe stock price tracking
├── 📄 Order.java               # Immutable order data structure
└── 📄 OrderType.java           # BUY/SELL enumeration
```

## 🔧 Key Features

### 🛡️ Thread Safety
- Race condition prevention using proper synchronization
- Deadlock avoidance through ordered lock acquisition
- Fair resource access using fair locks

### ⚡ Performance Optimization
- Read-heavy optimization using `ReadWriteLocks`
- Lock-free operations using atomic variables
- Efficient thread pools for resource management

### 📊 Real-time Monitoring
- Live order tracking with timestamped logs
- Trading statistics updated in real-time
- Stock price fluctuations simulating market volatility

### 🔄 Realistic Market Simulation
- Price-time priority matching (realistic exchange rules)
- Partial order fills when quantities don't match exactly
- Market volatility through random price movements  


## 📄 License

This project is licensed under the **MIT License** 
