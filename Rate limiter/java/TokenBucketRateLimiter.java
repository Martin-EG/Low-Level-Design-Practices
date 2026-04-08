import java.util.HashMap;
import java.util.Map;

public class TokenBucketRateLimiter implements RateLimiterStrategy{
    private int bucketDefaultCapacity;
    private int bucketDefaultRefillRate;
    private int inactivityLimit;
    private Map<String, TokenBucket> tokenBucketsByUsers = new HashMap<>();

    public TokenBucketRateLimiter(int bucketDefaultCapacity, int bucketDefaultRefillRate, int inactivityLimit) {
        this.bucketDefaultCapacity = bucketDefaultCapacity;
        this.bucketDefaultRefillRate = bucketDefaultRefillRate;
        this.inactivityLimit = inactivityLimit;
    }

    public void cleanUpOldTokenBuckets(int timestamp) {
        this.tokenBucketsByUsers.forEach((userId, tokenBucket) -> {
            if(timestamp - tokenBucket.getLastUsed() >= this.inactivityLimit) {
                this.tokenBucketsByUsers.remove(userId);
            }
        });
    }

    public boolean allowRequest(String userId, int timestamp) {
        this.cleanUpOldTokenBuckets(timestamp);

        TokenBucket tokenBucket = this.tokenBucketsByUsers.get(userId);

        if(tokenBucket == null) {
            tokenBucket = new TokenBucket(this.bucketDefaultCapacity, this.bucketDefaultRefillRate);
        }

        if(!tokenBucket.tryConsume(timestamp)) return false;

        this.tokenBucketsByUsers.put(userId, tokenBucket);
        return true;
    }
}
