import java.util.*;

/**
 * Strategy Interface
 */
interface RateLimitStrategy {
    boolean allowRequest();
}

/**
 * Context Class
 */
class RateLimiter {

    private final RateLimitStrategy strategy;

    public RateLimiter(RateLimitStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean allowRequest() {
        return strategy.allowRequest();
    }
}

/**
 * Token Bucket
 */
class TokenBucketStrategy implements RateLimitStrategy {

    private final long capacity;
    private final long refillRate;
    private long tokens;
    private long lastRefillTimestamp;

    public TokenBucketStrategy(long capacity, long refillRate) {

        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    private void refill() {

        long now = System.currentTimeMillis();
        long elapsed = now - lastRefillTimestamp;
        long newTokens = (elapsed * refillRate) / 1000;

        if (newTokens > 0) {
            tokens = Math.min(capacity, tokens + newTokens);
            lastRefillTimestamp = now;
        }
    }

    @Override
    public synchronized boolean allowRequest() {

        refill();

        if (tokens > 0) {
            tokens--;
            return true;
        }

        return false;
    }
}

/**
 * Leaky Bucket
 */
class LeakyBucketStrategy implements RateLimitStrategy {

    private final int capacity;
    private final int leakRate;
    private final Queue<Long> bucket = new LinkedList<>();
    private long lastLeakTime;

    public LeakyBucketStrategy(int capacity, int leakRate) {

        this.capacity = capacity;
        this.leakRate = leakRate;
        this.lastLeakTime = System.currentTimeMillis();
    }

    private void leak() {

        long now = System.currentTimeMillis();
        long elapsed = now - lastLeakTime;
        long leaked = (elapsed * leakRate) / 1000;

        while (leaked > 0 && !bucket.isEmpty()) {
            bucket.poll();
            leaked--;
        }

        if (elapsed > 0) {
            lastLeakTime = now;
        }
    }

    @Override
    public synchronized boolean allowRequest() {
        leak();

        if (bucket.size() < capacity) {
            bucket.offer(System.currentTimeMillis());
            return true;
        }

        return false;
    }
}


/**
 * Fixed Window Counter
 */
class FixedWindowStrategy implements RateLimitStrategy {

    private final int maxRequests;
    private final long windowSizeMillis;
    private int requestCount;
    private long windowStart;

    public FixedWindowStrategy(int maxRequests,
                               long windowSizeMillis) {

        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeMillis;

        this.windowStart = System.currentTimeMillis();
        this.requestCount = 0;
    }

    @Override
    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();

        if (now - windowStart >= windowSizeMillis) {
            requestCount = 0;
            windowStart = now;
        }

        if (requestCount < maxRequests) {
            requestCount++;
            return true;
        }

        return false;
    }
}

/**
 * Sliding Window Log
 */
class SlidingWindowLogStrategy implements RateLimitStrategy {

    private final int limit;
    private final long windowMillis;
    private final Queue<Long> timestamps = new LinkedList<>();

    public SlidingWindowLogStrategy(int limit, long windowMillis) {
        this.limit = limit;
        this.windowMillis = windowMillis;
    }

    @Override
    public synchronized boolean allowRequest() {
        long now = System.currentTimeMillis();

        while (!timestamps.isEmpty() && now - timestamps.peek() >= windowMillis) {
            timestamps.poll();
        }

        if (timestamps.size() < limit) {
            timestamps.offer(now);
            return true;
        }

        return false;
    }
}

/**
 * Sliding Window Counter
 */
class SlidingWindowCounterStrategy implements RateLimitStrategy {

    private final int limit;
    private final long windowSize;
    private int currentCount;
    private int previousCount;
    private long currentWindowStart;

    public SlidingWindowCounterStrategy(int limit, long windowSize) {
        this.limit = limit;
        this.windowSize = windowSize;
        currentWindowStart = System.currentTimeMillis();
    }

    @Override
    public synchronized boolean allowRequest() {

        long now = System.currentTimeMillis();
        long elapsed = now - currentWindowStart;

        if (elapsed >= windowSize) {
            previousCount = currentCount;
            currentCount = 0;

            currentWindowStart = now;
            elapsed = 0;
        }

        double overlap = (double) (windowSize - elapsed) / windowSize;
        double effectiveCount = currentCount + previousCount * overlap;

        if (effectiveCount >= limit) {
            return false;
        }

        currentCount++;
        return true;
    }
}

/**
 * Factory
 */
class RateLimiterFactory {

    public static RateLimiter fixedWindow(int limit, long windowMillis) {
        return new RateLimiter(new FixedWindowStrategy(limit, windowMillis));
    }

    public static RateLimiter tokenBucket(long capacity, long refillRate) {
        return new RateLimiter(new TokenBucketStrategy(capacity, refillRate));
    }

    public static RateLimiter slidingWindowLog(int limit, long windowMillis) {
        return new RateLimiter(new SlidingWindowLogStrategy(limit, windowMillis));
    }

    public static RateLimiter slidingWindowCounter(int limit, long windowMillis) {
        return new RateLimiter(new SlidingWindowCounterStrategy(limit, windowMillis));
    }

    public static RateLimiter leakyBucket(int capacity, int leakRate) {
        return new RateLimiter(new LeakyBucketStrategy(capacity, leakRate));
    }
}

/**
 * Demo
 */
public class RateLimiterClient {

    public static void main(String[] args) {
        RateLimiter limiter = RateLimiterFactory.tokenBucket(
                5, // capacity
                2 // refill/sec
        );

        for (int i = 1; i <= 10; i++) {
            System.out.println("Request " + i + " -> " + limiter.allowRequest());
        }
    }
}