public interface RateLimiterStrategy {
    boolean allowRequest(String userId, int timestamp);
}
