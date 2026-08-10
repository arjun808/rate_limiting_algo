// Main.java
public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Test 1: Fill up to capacity ===");
        LeakyBucket bucket1 = new LeakyBucket(5, 2);
        for (int i = 1; i <= 5; i++) {
            System.out.println("Request " + i + " allowed? " + bucket1.tryConsume());
        }
        System.out.println("Request 6 allowed? " + bucket1.tryConsume() + " (expected false, overflow)");

        System.out.println();
        System.out.println("=== Test 2: Leak over time ===");
        LeakyBucket bucket2 = new LeakyBucket(5, 5);
        for (int i = 0; i < 5; i++) bucket2.tryConsume();
        System.out.println("Bucket full. Immediate request allowed? " + bucket2.tryConsume() + " (expected false)");
        System.out.println("Waiting 300ms for leak...");
        Thread.sleep(300);
        System.out.println("After 300ms, request allowed? " + bucket2.tryConsume() + " (expected true)");

        System.out.println();
        System.out.println("=== Test 3: Weighted cost consumption ===");
        LeakyBucket bucket3 = new LeakyBucket(10, 2);
        System.out.println("Consume cost 4: " + bucket3.tryConsume(4) + " (expected true)");
        System.out.println("Consume cost 4: " + bucket3.tryConsume(4) + " (expected true)");
        System.out.println("Consume cost 4: " + bucket3.tryConsume(4) + " (expected false, would overflow)");
        System.out.println("Consume cost 2: " + bucket3.tryConsume(2) + " (expected true)");

        System.out.println();
        System.out.println("=== Test 4: Concurrent access (thread safety) ===");
        LeakyBucket bucket4 = new LeakyBucket(100, 0);
        final int threadCount = 10;
        final int requestsPerThread = 20;
        java.util.concurrent.atomic.AtomicInteger successCount = new java.util.concurrent.atomic.AtomicInteger(0);
        Thread[] threads = new Thread[threadCount];

        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < requestsPerThread; j++) {
                    if (bucket4.tryConsume()) {
                        successCount.incrementAndGet();
                    }
                }
            });
        }

        for (Thread t : threads) t.start();
        for (Thread t : threads) t.join();

        System.out.println("Total successful consumes: " + successCount.get() + " (expected exactly 100)");

        System.out.println();
        System.out.println("=== All tests complete ===");
    }
}