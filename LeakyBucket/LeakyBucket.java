import java.util.concurrent.locks.ReentrantLock;

public class LeakyBucket {
    private final long capacity;
    private final double leakRatePerNs;
    private double currentLevel;
    private long lastLeakTimestamp;
    private final ReentrantLock lock = new ReentrantLock();

    public LeakyBucket(long capacity, double leakRatePerSecond) {
        this.capacity = capacity;
        this.currentLevel = 0;
        this.leakRatePerNs = leakRatePerSecond / 1_000_000_000.0;
        this.lastLeakTimestamp = System.nanoTime();
    }

    public boolean tryConsume() {
        return tryConsume(1);
    }

    public boolean tryConsume(int cost) {
        lock.lock();
        try {
            leak();
            if (currentLevel + cost <= capacity) {
                currentLevel += cost;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    private void leak() {
        long now = System.nanoTime();
        long elapsed = now - lastLeakTimestamp;
        double leaked = elapsed * leakRatePerNs;
        if (leaked > 0) {
            currentLevel = Math.max(0, currentLevel - leaked);
            lastLeakTimestamp = now;
        }
    }
}