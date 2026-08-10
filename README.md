# Rate Limiting Algorithms (Java)

A collection of classic rate limiting algorithms implemented in Java — thread-safe, dependency-free, and built for both learning and real-world use.

## Why this repo?

Rate limiting is a core building block in backend systems (API throttling, request shaping, protecting downstream services). This repo implements the well-known algorithms from scratch, with a focus on:

- **Correctness** — no race conditions under concurrent access
- **Clean, minimal code** — no external dependencies
- **Practical usage** — designed to be dropped into a real project, not just a toy demo

## Algorithms

| Algorithm | Status | Description |
|---|---|---|
| Token Bucket | ✅ Implemented | Tokens refill continuously at a fixed rate up to a capacity; each request consumes tokens. Allows bursts up to capacity. |
| Leaky Bucket | ✅ Implemented | Requests add to a "water level" that leaks out at a fixed constant rate; overflow is rejected. Smooths output instead of allowing bursts. |
| Fixed Window Counter | 🚧 Planned | Counts requests in fixed time windows (e.g. per minute), resets each window. |
| Sliding Window Log | 🚧 Planned | Tracks precise request timestamps within a rolling window. |
| Sliding Window Counter | 🚧 Planned | Approximates sliding window behavior using weighted counts across two fixed windows. |

## Project Structure

```
.
├── TokenBucket.java     # Token bucket implementation
├── LeakyBucket.java      # Leaky bucket implementation
├── Main.java             # Test/demo harness
└── README.md
```

## Token Bucket — How it works

- A bucket holds up to `capacity` tokens.
- Tokens refill continuously over time at `refillRatePerSecond`.
- Each call to `tryConsume()` (or `tryConsume(n)` for weighted costs) checks if enough tokens are available:
  - If yes → tokens are deducted, request is **allowed**.
  - If no → request is **rejected**.
- Refill is *lazy*: instead of a background thread ticking constantly, tokens are calculated based on elapsed time (via `System.nanoTime()`) the moment a request comes in.
- Thread safety is handled with a `ReentrantLock` around the check-and-consume logic.

### Usage

```java
// capacity = 5 tokens, refill rate = 5 tokens/second
TokenBucket bucket = new TokenBucket(5, 5);

if (bucket.tryConsume()) {
    // allow request
} else {
    // reject / throttle request
}

// consume a weighted number of tokens for a more "expensive" request
if (bucket.tryConsume(3)) {
    // allow request
}
```

## Leaky Bucket — How it works

- A bucket holds a "water level" that starts empty and can rise up to `capacity`.
- The level leaks (drains) continuously over time at `leakRatePerSecond`.
- Each call to `tryConsume()` (or `tryConsume(cost)` for weighted costs):
  - First leaks the bucket based on elapsed time.
  - Then checks if adding this request's cost would exceed `capacity`:
    - If no → level increases, request is **allowed**.
    - If yes → request is **rejected** (overflow).
- Unlike Token Bucket (which allows bursts up to capacity), Leaky Bucket is about smoothing — the level only ever drains at a fixed rate, so it does not "save up" allowance for future bursts.
- Same lazy, on-demand time calculation and `ReentrantLock`-based thread safety as Token Bucket.

### Usage

```java
// capacity = 5, leak rate = 2 per second
LeakyBucket bucket = new LeakyBucket(5, 2);

if (bucket.tryConsume()) {
    // allow request
} else {
    // reject — bucket would overflow
}

// weighted cost for a more "expensive" request
if (bucket.tryConsume(3)) {
    // allow request
}
```

### Running the demo

```bash
javac TokenBucket.java LeakyBucket.java Main.java
java Main
```

`Main.java` currently exercises the Leaky Bucket implementation:
1. Fill up to capacity / overflow rejection
2. Leak behavior over time
3. Weighted cost consumption
4. Thread safety under concurrent access

## Roadmap

- [x] Implement Token Bucket
- [x] Implement Leaky Bucket
- [ ] Implement Fixed Window Counter
- [ ] Implement Sliding Window Log
- [ ] Implement Sliding Window Counter
- [ ] Add benchmarks comparing algorithms
- [ ] Add unit tests (JUnit)

## Contributing

This is primarily a learning/reference repo. Issues and PRs for bug fixes, additional algorithms, or test coverage are welcome.

## License

MIT