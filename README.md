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
| Leaky Bucket | 🚧 Planned | Requests are processed at a fixed output rate, smoothing out bursts. |
| Fixed Window Counter | 🚧 Planned | Counts requests in fixed time windows (e.g. per minute), resets each window. |
| Sliding Window Log | 🚧 Planned | Tracks precise request timestamps within a rolling window. |
| Sliding Window Counter | 🚧 Planned | Approximates sliding window behavior using weighted counts across two fixed windows. |

## Project Structure

```
.
├── TokenBucket.java     # Token bucket implementation
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

### Running the demo

```bash
javac Main.java TokenBucket.java
java Main
```

`Main.java` exercises:
1. Basic consumption up to capacity
2. Refill behavior over time
3. Weighted token consumption
4. Thread safety under concurrent access

## Roadmap

- [ ] Implement Leaky Bucket
- [ ] Implement Fixed Window Counter
- [ ] Implement Sliding Window Log
- [ ] Implement Sliding Window Counter
- [ ] Add benchmarks comparing algorithms
- [ ] Add unit tests (JUnit)

## Contributing

This is primarily a learning/reference repo. Issues and PRs for bug fixes, additional algorithms, or test coverage are welcome.

## License

MIT# rate_limiting_algo
