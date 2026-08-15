import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.locks.ReentrantLock;

public class SlidingWindowLog {
    private final int limit;
    private final long windowSizeNs;
    private final Deque<Long> timestamps = new ArrayDeque<>();
    private final ReentrantLock lock = new ReentrantLock();

    public SlidingWindowLog(int limit, long windowSizeNs) {
        if (limit <= 0) {
            throw new IllegalArgumentException("limit must be positive");
        }
        if (windowSizeNs <= 0) {
            throw new IllegalArgumentException("windowSizeNs must be positive");
        }
        this.limit = limit;
        this.windowSizeNs = windowSizeNs;
    }

    public boolean tryConsume() {
        lock.lock();
        try {
            long now = System.nanoTime();
            long windowStart = now - windowSizeNs;

            // evict anything outside the window
            while (!timestamps.isEmpty() && timestamps.peekFirst() < windowStart) {
                timestamps.pollFirst();
            }

            if (timestamps.size() < limit) {
                timestamps.addLast(now);
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
}