import java.util.concurrent.locks.ReentrantLock;

public class FixedWindowCounter {
    private final int limit;
    private final long windowSizeMs;
    private long currentWindowStart;
    private int currentCount;
    private final ReentrantLock lock = new ReentrantLock();

    public FixedWindowCounter(int limit, long windowSizeMs) {
        this.limit = limit;
        this.windowSizeMs = windowSizeMs;
        this.currentWindowStart = System.currentTimeMillis() / windowSizeMs * windowSizeMs;
        this.currentCount = 0;
    }

    public boolean tryConsume() {
        lock.lock();
        try {
            long now = System.currentTimeMillis();

            if (now - currentWindowStart >= windowSizeMs) {
                currentWindowStart = now / windowSizeMs * windowSizeMs;
                currentCount = 0;
            }

            if (currentCount < limit) {
                currentCount++;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public String debugState(long now) {
        return String.format(
            "windowStart=%d, currentCount=%d, limit=%d",
            currentWindowStart, currentCount, limit
        );
    }
}