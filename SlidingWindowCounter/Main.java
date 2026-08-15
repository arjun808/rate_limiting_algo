public class SlidingWindowCounterDemo {
    public static void main(String[] args) throws InterruptedException {
        int limit = 5;
        long windowSizeMs = 2000; // 2 seconds, so test runs fast

        SlidingWindowCounter limiter = new SlidingWindowCounter(limit, windowSizeMs);

        System.out.println("=== Phase 1: burst fill within same window ===");
        for (int i = 1; i <= 7; i++) {
            boolean allowed = limiter.tryConsume();
            System.out.printf("Request %d -> allowed=%s%n", i, allowed);
        }
        // With limit=5, requests 6 and 7 should be rejected (still in same window)

        System.out.println("\n=== Phase 2: wait 1s (half window), try again ===");
        Thread.sleep(1000);
        for (int i = 8; i <= 9; i++) {
            boolean allowed = limiter.tryConsume();
            System.out.printf("Request %d -> allowed=%s%n", i, allowed);
        }
        // Still likely rejected/near-limit — previous window's weight still counted

        System.out.println("\n=== Phase 3: wait for rollover into next window ===");
        Thread.sleep(1500); // total ~2.5s since phase1 start -> new window
        for (int i = 10; i <= 12; i++) {
            boolean allowed = limiter.tryConsume();
            System.out.printf("Request %d -> allowed=%s%n", i, allowed);
        }
        // Previous window's weight fading — some should now be allowed

        System.out.println("\n=== Phase 4: long idle gap (2+ windows), should fully reset ===");
        Thread.sleep(5000); // way more than 2 windows (2000ms each)
        for (int i = 13; i <= 15; i++) {
            boolean allowed = limiter.tryConsume();
            System.out.printf("Request %d -> allowed=%s%n", i, allowed);
        }
        // Should behave like a fresh limiter — first few allowed easily
    }
}