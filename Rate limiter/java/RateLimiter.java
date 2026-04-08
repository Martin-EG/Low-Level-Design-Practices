public class RateLimiter {
    private RateLimiterStrategy rateLimiterStrategy;
    
    public RateLimiter(RateLimiterStrategy rateLimiterStrategy) {
        this.rateLimiterStrategy = rateLimiterStrategy;
    }
    
    public boolean allowRequest(String userId, int timestamp) {
        return this.rateLimiterStrategy.allowRequest(userId, timestamp);
    }
}