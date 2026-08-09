import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

public class TokenBucket {
    private final long capacity;
    private final double refillRatePerNs; 
    private double availableTokens;
    private long lastRefillTimestamp;
    private final ReentrantLock lock = new ReentrantLock();

    public TokenBucket(long capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.availableTokens = capacity; 
        this.refillRatePerNs = refillRatePerSecond / 1_000_000_000.0;
        this.lastRefillTimestamp = System.nanoTime();
    }

    public boolean tryConsume() {
        return tryConsume(1);
    }

    public boolean tryConsume(int tokens) {
        lock.lock();
        try {
            refill();
            if (availableTokens >= tokens) {
                availableTokens -= tokens;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void refill() {
        long now = System.nanoTime();
        long elapsed = now - lastRefillTimestamp;
        double tokensToAdd = elapsed * refillRatePerNs;
        if (tokensToAdd > 0) {
            availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
            lastRefillTimestamp = now;
        }
    }
}