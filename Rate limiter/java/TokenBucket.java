public class TokenBucket {
    private int capacity;
    private int tokens;
    private int refillRatePerSecond;
    private int lastRefillTime = 0;
    private int lastUsedTime = 0;

    public TokenBucket(int capacity, int refillRatePerSecond) {
        this.capacity = capacity;
        this.tokens = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    public void updateLastUsed(int timestamp) {
        this.lastUsedTime = timestamp;
    }

    public int getLastUsed() {
        return this.lastUsedTime;
    }

    public void refillTokens(int timestamp) {
        if(this.lastRefillTime == 0) {
            this.refillRatePerSecond = timestamp;
            return;
        }

        int timeSinceLastRefill = timestamp - this.lastRefillTime;
        int tokensToAdd = (int) Math.floor(timeSinceLastRefill * this.refillRatePerSecond);

        this.tokens = Math.min(this.capacity, tokensToAdd);
    }

    public boolean tryConsume(int timestamp) {
        this.refillTokens(timestamp);
        this.updateLastUsed(timestamp);

        if(this.tokens < 1) return false;

        this.tokens--;
        return true;
    }
}
