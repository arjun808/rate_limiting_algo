import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindowCounter {
    private final int limit;
    private final long windowSizeMs;
    private long currentWindowStart;
    private int currentCount;
    private int previousCount;
    private final ReentrantLock lock = new ReentrantLock();

    public SlidingWindowCounter(int limit, long windowSizeMs) {
        this.limit = limit;
        this.windowSizeMs = windowSizeMs;
        this.currentWindowStart = System.currentTimeMillis() / windowSizeMs * windowSizeMs;
    }

    public boolean tryConsume() {
        lock.lock();
        try {
            long now = System.currentTimeMillis();
            long windowsElapsed = (now - currentWindowStart) / windowSizeMs;

            if (windowsElapsed >= 2) {
                // it's been a while — reset both counts
                currentCount = 0;
                previousCount = 0;
                currentWindowStart = now / windowSizeMs * windowSizeMs;
            } else if (windowsElapsed == 1) {
                // roll over: current becomes previous
                previousCount = currentCount;
                currentCount = 0;
                currentWindowStart += windowSizeMs;
            }

            double elapsedInCurrent = now - currentWindowStart;
            double weight = 1.0 - (elapsedInCurrent / windowSizeMs);
            double estimatedCount = previousCount * weight + currentCount;

            if (estimatedCount < limit) {
                currentCount++;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}