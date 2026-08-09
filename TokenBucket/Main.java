public class Main {

    public static void main(String[] args) throws InterruptedException {
        System.out.println("=== Test 1: Basic consumption within capacity ===");
        TokenBucket bucket1 = new TokenBucket(5, 1); 
        for (int i = 1; i <= 5; i++) {
            System.out.println("Request " + i + " allowed? " + bucket1.tryConsume());
        }
        System.out.println("Request 6 allowed? " + bucket1.tryConsume() + " (expected false, bucket exhausted)");

        System.out.println();
        System.out.println("=== Test 2: Refill over time ===");
        TokenBucket bucket2 = new TokenBucket(5, 5); 
        for (int i = 0; i < 5; i++) bucket2.tryConsume();
        System.out.println("Bucket drained. Immediate request allowed? " + bucket2.tryConsume() + " (expected false)");

        System.out.println("Waiting 300ms for refill...");
        Thread.sleep(300);
        System.out.println("After 300ms, request allowed? " + bucket2.tryConsume() + " (expected true)");

        System.out.println();
        System.out.println("=== Test 3: Weighted token consumption ===");
        TokenBucket bucket3 = new TokenBucket(10, 2); // capacity 10, refill 2 tokens/sec
        System.out.println("Consume 4 tokens: " + bucket3.tryConsume(4) + " (expected true)");
        System.out.println("Consume 4 tokens: " + bucket3.tryConsume(4) + " (expected true)");
        System.out.println("Consume 4 tokens: " + bucket3.tryConsume(4) + " (expected false, only 2 left)");
        System.out.println("Consume 2 tokens: " + bucket3.tryConsume(2) + " (expected true)");

        System.out.println();
        System.out.println("=== Test 4: Concurrent access (thread safety) ===");
        TokenBucket bucket4 = new TokenBucket(100, 0); // capacity 100, no refill
        final int threadCount = 10;
        final int requestsPerThread = 20; // 200 total requests, only 100 tokens available
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