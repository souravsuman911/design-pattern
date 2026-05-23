# Java Implementation Concepts for LLD

This file explains Java concepts that are repeatedly useful while implementing LLD problems such as parking lot, event booking, inventory management, rate limiter, notification system, cab booking, wallet, and file system.

## 1. Concurrency Basics

### Why Concurrency Matters
Many LLD problems have shared resources.

Examples:
- Two users booking the same event seat.
- Two cars trying to take the same parking spot.
- Two orders buying the last inventory item.
- Two wallet transfers updating the same account.
- Multiple producers adding notification jobs.

The goal is to keep shared state correct when multiple threads act at the same time.

### Race Condition
A race condition happens when output depends on unpredictable thread timing.

Example:
```java
if (seat.isAvailable()) {
    seat.book(userId);
}
```

Problem: two threads can both see the seat as available before either books it.

### Critical Section
A critical section is code that must be executed by only one thread at a time.

Example:
```java
lock.lock();
try {
    if (seat.isAvailable()) {
        seat.book(userId);
    }
} finally {
    lock.unlock();
}
```

## 2. Pessimistic Locking

### Meaning
Pessimistic locking assumes conflicts are likely, so it locks the resource before checking/updating it.

Use when:
- Conflict probability is high.
- Resource is scarce.
- Correctness is more important than throughput.

Good examples:
- Popular movie/event seat booking.
- Wallet transfer on same account.
- Limited parking spot assignment.

### Java Implementation Using `synchronized`
```java
class Seat {
    private boolean booked;

    public synchronized boolean book() {
        if (booked) return false;
        booked = true;
        return true;
    }
}
```

Simple, but less flexible than `Lock`.

### Java Implementation Using `ReentrantLock`
```java
import java.util.concurrent.locks.ReentrantLock;

class Seat {
    private final ReentrantLock lock = new ReentrantLock();
    private boolean booked;

    public boolean book() {
        lock.lock();
        try {
            if (booked) return false;
            booked = true;
            return true;
        } finally {
            lock.unlock();
        }
    }
}
```

### Why `finally` Is Important
Always unlock in `finally`; otherwise an exception can keep the lock forever.

```java
lock.lock();
try {
    // critical section
} finally {
    lock.unlock();
}
```

### `tryLock()` Example
`tryLock()` avoids waiting forever.

```java
if (lock.tryLock()) {
    try {
        // book resource
    } finally {
        lock.unlock();
    }
} else {
    // tell user: resource is busy, retry later
}
```

Useful for:
- Seat hold attempts.
- Driver assignment.
- Avoiding deadlock in multi-resource flows.

## 3. Optimistic Locking

### Meaning
Optimistic locking assumes conflicts are rare. It does not lock first. Instead, it checks whether the resource changed before saving.

Use when:
- Conflicts are not very frequent.
- You want better throughput.
- Retry is acceptable.

Good examples:
- Inventory update with moderate traffic.
- Profile update.
- Meeting room booking with low conflict.

### Version-Based Example
```java
class InventoryItem {
    private int quantity;
    private int version;

    public int getQuantity() { return quantity; }
    public int getVersion() { return version; }
}
```

Pseudo-update:
```sql
UPDATE inventory
SET quantity = quantity - 1,
    version = version + 1
WHERE id = ?
  AND quantity > 0
  AND version = ?;
```

If updated row count is `0`, someone else changed it. Reload and retry or fail gracefully.

### In-Memory Java Example Using `AtomicInteger`
```java
import java.util.concurrent.atomic.AtomicInteger;

class Stock {
    private final AtomicInteger quantity = new AtomicInteger(10);

    public boolean reserveOne() {
        while (true) {
            int current = quantity.get();
            if (current <= 0) return false;
            if (quantity.compareAndSet(current, current - 1)) {
                return true;
            }
        }
    }
}
```

### `compareAndSet`
`compareAndSet(expected, newValue)` updates only if current value still equals expected value.

This is optimistic locking at variable level.

## 4. `synchronized` vs `ReentrantLock`

| Concept | `synchronized` | `ReentrantLock` |
|---|---|---|
| Syntax | Simpler | More code |
| Unlock | Automatic | Manual in `finally` |
| Try lock | Not available | `tryLock()` available |
| Interruptible wait | Limited | `lockInterruptibly()` |
| Fairness | Not configurable | Can create fair lock |
| Conditions | One monitor wait-set | Multiple `Condition`s |

### When To Use
- Use `synchronized` for simple critical sections.
- Use `ReentrantLock` when you need timeout, fairness, interruptible locking, or multiple conditions.

## 5. Semaphore

### Meaning
A semaphore controls how many threads can access a resource at the same time.

Use when:
- There are N identical resources.
- You want to limit concurrency.

Examples:
- Parking lot has 100 total spots.
- API allows 10 concurrent requests.
- Connection pool has 20 connections.

### Java Example
```java
import java.util.concurrent.Semaphore;

class ParkingCapacity {
    private final Semaphore spots;

    public ParkingCapacity(int capacity) {
        this.spots = new Semaphore(capacity);
    }

    public boolean enter() throws InterruptedException {
        return spots.tryAcquire();
    }

    public void exit() {
        spots.release();
    }
}
```

### Important Point
Semaphore controls count, not identity. If you need a specific spot ID, combine semaphore with spot allocation logic.

```text
Semaphore says: one spot is available.
Allocator says: assign spot P12.
```

## 6. ReadWriteLock

### Meaning
Allows multiple readers but only one writer.

Use when:
- Reads are frequent.
- Writes are less frequent.

Examples:
- Search available rooms while admin updates room data.
- Read product catalog while seller updates price.
- Read file metadata while update is rare.

### Java Example
```java
import java.util.*;
import java.util.concurrent.locks.*;

class Catalog {
    private final Map<String, String> products = new HashMap<>();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public String getProduct(String id) {
        lock.readLock().lock();
        try {
            return products.get(id);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void updateProduct(String id, String name) {
        lock.writeLock().lock();
        try {
            products.put(id, name);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

## 7. BlockingQueue

### Meaning
A `BlockingQueue` is a thread-safe queue where:
- Producer waits if queue is full.
- Consumer waits if queue is empty.

Use when:
- Work is produced by one part of system and consumed by another.

Examples:
- Notification delivery queue.
- Order processing queue.
- Logging queue.
- Background expiry cleanup jobs.

### Java Built-In Example
```java
import java.util.concurrent.*;

class NotificationWorker {
    private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();

    public void publish(String message) throws InterruptedException {
        queue.put(message); // waits if bounded queue is full
    }

    public void startConsumer() {
        new Thread(() -> {
            while (true) {
                try {
                    String message = queue.take(); // waits if empty
                    System.out.println("Sending: " + message);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        }).start();
    }
}
```

### Common Implementations
- `ArrayBlockingQueue`: bounded, array-backed.
- `LinkedBlockingQueue`: optionally bounded, linked nodes.
- `PriorityBlockingQueue`: priority-based tasks.
- `DelayQueue`: tasks become available after delay.

### When To Use Which
- Use `ArrayBlockingQueue` for fixed-size worker queues.
- Use `LinkedBlockingQueue` for general producer-consumer use.
- Use `PriorityBlockingQueue` for urgent notifications or priority jobs.
- Use `DelayQueue` for hold expiry or scheduled retry.

## 8. Implementing a Simple Blocking Queue

This is useful to understand `wait()` and `notifyAll()`.

```java
import java.util.*;

class SimpleBlockingQueue<T> {
    private final Queue<T> queue = new LinkedList<>();
    private final int capacity;

    public SimpleBlockingQueue(int capacity) {
        this.capacity = capacity;
    }

    public synchronized void put(T item) throws InterruptedException {
        while (queue.size() == capacity) {
            wait();
        }
        queue.offer(item);
        notifyAll();
    }

    public synchronized T take() throws InterruptedException {
        while (queue.isEmpty()) {
            wait();
        }
        T item = queue.poll();
        notifyAll();
        return item;
    }
}
```

### Why `while`, Not `if`
Always use `while` around `wait()` because of spurious wakeups and because another thread may consume/produce before the current thread resumes.

## 9. ExecutorService and Thread Pools

### Meaning
`ExecutorService` manages worker threads for executing tasks.

Use instead of manually creating many threads.

Examples:
- Process notification queue.
- Run payment callback handlers.
- Expire old seat holds.
- Process order fulfillment tasks.

### Java Example
```java
import java.util.concurrent.*;

ExecutorService executor = Executors.newFixedThreadPool(5);

executor.submit(() -> {
    System.out.println("Process task");
});

executor.shutdown();
```

### Common Pools
- `newFixedThreadPool(n)`: fixed workers.
- `newSingleThreadExecutor()`: ordered sequential processing.
- `newCachedThreadPool()`: dynamic threads, use carefully.
- `ScheduledExecutorService`: delayed or repeated tasks.

### Scheduled Task Example
```java
ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

scheduler.scheduleAtFixedRate(() -> {
    System.out.println("Release expired holds");
}, 0, 1, TimeUnit.MINUTES);
```

## 10. DelayQueue for Expiry Flows

### Use Case
Good for temporary holds, OTP expiry, payment session expiry, retry-after jobs.

### Compact Example
```java
import java.util.concurrent.*;

class ExpiringHold implements Delayed {
    private final String holdId;
    private final long expireAtMillis;

    ExpiringHold(String holdId, long delayMillis) {
        this.holdId = holdId;
        this.expireAtMillis = System.currentTimeMillis() + delayMillis;
    }

    public String getHoldId() {
        return holdId;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        long delay = expireAtMillis - System.currentTimeMillis();
        return unit.convert(delay, TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), other.getDelay(TimeUnit.MILLISECONDS));
    }
}
```

Consumer:
```java
DelayQueue<ExpiringHold> queue = new DelayQueue<>();
ExpiringHold expired = queue.take(); // returns only after delay expires
```

## 11. Concurrent Collections

### Why Needed
Normal collections like `HashMap` and `ArrayList` are not thread-safe for concurrent mutation.

### Useful Java Classes
- `ConcurrentHashMap`: thread-safe map with high concurrency.
- `CopyOnWriteArrayList`: good for many reads, few writes.
- `ConcurrentLinkedQueue`: non-blocking thread-safe queue.
- `BlockingQueue`: thread-safe producer-consumer queue.
- `ConcurrentSkipListMap`: sorted concurrent map.

### `ConcurrentHashMap` Example
```java
import java.util.concurrent.*;

class SeatStore {
    private final ConcurrentHashMap<String, Boolean> seatBooked = new ConcurrentHashMap<>();

    public boolean book(String seatId) {
        return seatBooked.putIfAbsent(seatId, true) == null;
    }
}
```

This is useful for simple in-memory uniqueness.

## 12. Atomic Variables

### Meaning
Atomic classes provide lock-free thread-safe operations on single variables.

Useful classes:
- `AtomicInteger`
- `AtomicLong`
- `AtomicBoolean`
- `AtomicReference`

### Example: Rate Limiter Counter
```java
import java.util.concurrent.atomic.AtomicInteger;

class Counter {
    private final AtomicInteger count = new AtomicInteger(0);

    public boolean allow(int limit) {
        int current = count.incrementAndGet();
        return current <= limit;
    }
}
```

### When To Use
Use atomic variables for simple counters, flags, and CAS-based updates. Do not use them for complex multi-object transactions.

## 13. Idempotency

### Meaning
Performing the same operation multiple times has the same effect as performing it once.

Important in:
- Payment callback.
- Booking confirmation retry.
- Order creation retry.
- Notification retry.

### Java/In-Memory Example
```java
import java.util.*;
import java.util.concurrent.*;

class BookingService {
    private final Set<String> processedKeys = ConcurrentHashMap.newKeySet();

    public boolean confirm(String idempotencyKey) {
        if (!processedKeys.add(idempotencyKey)) {
            return true; // already processed
        }
        // perform confirmation
        return true;
    }
}
```

In real systems, store idempotency keys in DB with unique constraint.

## 14. State Machine Implementation

### Enum-Based State Machine
```java
enum BookingStatus {
    HELD, CONFIRMED, CANCELLED, EXPIRED
}
```

```java
class Booking {
    private BookingStatus status = BookingStatus.HELD;

    public void confirm() {
        if (status != BookingStatus.HELD) {
            throw new IllegalStateException("Only held booking can be confirmed");
        }
        status = BookingStatus.CONFIRMED;
    }

    public void cancel() {
        if (status == BookingStatus.CONFIRMED || status == BookingStatus.HELD) {
            status = BookingStatus.CANCELLED;
            return;
        }
        throw new IllegalStateException("Cannot cancel booking in state " + status);
    }
}
```

### When To Use State Pattern
Use separate state classes when each state has different behavior.

Examples:
- Vending machine.
- ATM.
- Elevator.

## 15. Strategy Pattern in Java

### Meaning
Strategy makes algorithm replaceable.

Examples:
- `PricingStrategy`
- `AllocationStrategy`
- `MatchingStrategy`
- `DiscountStrategy`
- `RateLimitStrategy`

### Example
```java
interface PricingStrategy {
    int calculatePrice(int basePrice);
}

class PremiumSeatPricing implements PricingStrategy {
    public int calculatePrice(int basePrice) {
        return basePrice * 2;
    }
}

class NormalPricing implements PricingStrategy {
    public int calculatePrice(int basePrice) {
        return basePrice;
    }
}
```

Usage:
```java
class PriceService {
    public int price(PricingStrategy strategy, int basePrice) {
        return strategy.calculatePrice(basePrice);
    }
}
```

## 16. Producer-Consumer Pattern

### Meaning
Producer creates tasks; consumer processes tasks asynchronously.

Common in:
- Notifications.
- Order fulfillment.
- Log processing.
- Payment webhook processing.

### Java Example
```java
BlockingQueue<Runnable> queue = new LinkedBlockingQueue<>();

// producer
queue.put(() -> System.out.println("Send email"));

// consumer
Runnable task = queue.take();
task.run();
```

Usually, prefer `ExecutorService` unless you specifically need queue control.

## 17. Rate Limiter Java Implementation

### Simple Token Bucket
```java
class TokenBucketRateLimiter {
    private final int capacity;
    private final int refillTokensPerSecond;
    private int tokens;
    private long lastRefillTime;

    public TokenBucketRateLimiter(int capacity, int refillTokensPerSecond) {
        this.capacity = capacity;
        this.refillTokensPerSecond = refillTokensPerSecond;
        this.tokens = capacity;
        this.lastRefillTime = System.nanoTime();
    }

    public synchronized boolean allowRequest() {
        refill();
        if (tokens == 0) return false;
        tokens--;
        return true;
    }

    private void refill() {
        long now = System.nanoTime();
        long elapsedSeconds = (now - lastRefillTime) / 1_000_000_000;
        if (elapsedSeconds > 0) {
            int tokensToAdd = (int) elapsedSeconds * refillTokensPerSecond;
            tokens = Math.min(capacity, tokens + tokensToAdd);
            lastRefillTime = now;
        }
    }
}
```

Use `synchronized` here because `tokens` and `lastRefillTime` must be updated together.

## 18. Resource Allocation Java Sketch

### Seat Booking With Lock Per Seat
```java
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

class SeatBookingService {
    private final Map<String, ReentrantLock> locks = new ConcurrentHashMap<>();
    private final Set<String> bookedSeats = ConcurrentHashMap.newKeySet();

    public boolean bookSeat(String seatId) {
        ReentrantLock lock = locks.computeIfAbsent(seatId, id -> new ReentrantLock());
        lock.lock();
        try {
            if (bookedSeats.contains(seatId)) return false;
            bookedSeats.add(seatId);
            return true;
        } finally {
            lock.unlock();
        }
    }
}
```

### Why Lock Per Seat
Locking only the selected seat gives better concurrency than locking the whole booking service.

## 19. Inventory Java Sketch

### Atomic Decrement
```java
import java.util.concurrent.atomic.AtomicInteger;

class Inventory {
    private final AtomicInteger stock;

    public Inventory(int initialStock) {
        this.stock = new AtomicInteger(initialStock);
    }

    public boolean reserve(int quantity) {
        while (true) {
            int available = stock.get();
            if (available < quantity) return false;
            if (stock.compareAndSet(available, available - quantity)) {
                return true;
            }
        }
    }
}
```

Good for one item counter. For multiple SKUs/order items, use transaction/locks at service or DB level.

## 20. Deadlock

### Meaning
Deadlock happens when threads wait forever for each other.

Example:
```text
Thread 1 locks Account A, waits for Account B.
Thread 2 locks Account B, waits for Account A.
```

### Prevention
- Always lock resources in a fixed order.
- Use `tryLock()` with timeout.
- Keep critical sections small.
- Avoid calling external services while holding a lock.

### Account Transfer Example
```java
class Account {
    final int id;
    final ReentrantLock lock = new ReentrantLock();
    int balance;

    Account(int id, int balance) {
        this.id = id;
        this.balance = balance;
    }
}

class TransferService {
    public boolean transfer(Account from, Account to, int amount) {
        Account first = from.id < to.id ? from : to;
        Account second = from.id < to.id ? to : from;

        first.lock.lock();
        try {
            second.lock.lock();
            try {
                if (from.balance < amount) return false;
                from.balance -= amount;
                to.balance += amount;
                return true;
            } finally {
                second.lock.unlock();
            }
        } finally {
            first.lock.unlock();
        }
    }
}
```

## 21. Immutability and Value Objects

### Meaning
Immutable objects cannot change after creation.

Use for:
- `Money`
- `TimeRange`
- `Location`
- `Address`
- `SearchCriteria`

### Example
```java
final class Money {
    private final long amountInPaise;
    private final String currency;

    public Money(long amountInPaise, String currency) {
        this.amountInPaise = amountInPaise;
        this.currency = currency;
    }

    public Money add(Money other) {
        if (!currency.equals(other.currency)) {
            throw new IllegalArgumentException("Currency mismatch");
        }
        return new Money(amountInPaise + other.amountInPaise, currency);
    }
}
```

## 22. Choosing the Right Java Tool

| Problem Need | Java Tool |
|---|---|
| One thread at a time | `synchronized`, `ReentrantLock` |
| Try lock / timeout | `ReentrantLock.tryLock()` |
| N concurrent permits | `Semaphore` |
| Many reads, few writes | `ReadWriteLock` |
| Producer-consumer | `BlockingQueue` |
| Delayed expiry | `DelayQueue`, `ScheduledExecutorService` |
| Background workers | `ExecutorService` |
| Thread-safe map | `ConcurrentHashMap` |
| Atomic counter | `AtomicInteger`, `AtomicLong` |
| Ordered priority work | `PriorityBlockingQueue` |
| Retry-safe operation | Idempotency key + unique store |
| Lifecycle control | Enum state machine / State pattern |

## 23. LLD Problem Mapping

### Resource Allocation
Know:
- `ReentrantLock`
- `Semaphore`
- Optimistic vs pessimistic locking
- Hold expiry using `DelayQueue` or scheduler
- Idempotency for payment confirmation

### Inventory Management
Know:
- Atomic decrement
- Optimistic locking/versioning
- Reservation with TTL
- Stock ledger idea
- Order state machine

### Notification System
Know:
- `BlockingQueue`
- `ExecutorService`
- Retry with backoff
- Deduplication/idempotency
- Channel strategy pattern

### Rate Limiter
Know:
- `AtomicInteger`
- `ConcurrentHashMap`
- Token bucket
- Sliding window
- TTL cleanup

### Cab/Provider Assignment
Know:
- Lock provider while assigning
- Priority queues for ranking
- Timeout and retry
- Strategy pattern for matching

### Wallet/Banking
Know:
- Lock ordering to prevent deadlock
- Atomic transfer
- Immutable ledger entries
- Idempotency key
- Money value object

## 24. Quick Interview Phrases

- **Pessimistic locking**: Lock first because conflict is likely.
- **Optimistic locking**: Update with version check because conflict is rare.
- **Semaphore**: Controls number of concurrent permits, not exact resource identity.
- **BlockingQueue**: Decouples producer and consumer with thread-safe waiting.
- **AtomicInteger**: Good for single counter; not enough for multi-object transactions.
- **ReentrantLock**: More flexible than `synchronized`, supports `tryLock` and fairness.
- **Idempotency**: Same request repeated should not repeat side effects.
- **Deadlock prevention**: Lock in fixed order or use timed `tryLock`.
- **State machine**: Explicitly model valid transitions and reject invalid ones.
