class TokenBucket {
  private capacity: number;
  private tokens: number;
  private refillRatePerSecond: number;
  private lastRefillTime: number = 0;
  private lastUsedTime: number = 0;

  constructor(capacity: number, refillRatePerSecond: number) {
    this.capacity = capacity;
    this.tokens = capacity;
    this.refillRatePerSecond = refillRatePerSecond;
  }

  updateLastUsed(timestamp: number): void {
    this.lastUsedTime = timestamp;
  }

  getLastUsed(): number {
    return this.lastUsedTime;
  }

  refillTokens(timestamp: number): void {
    if (this.lastRefillTime === 0) {
      this.lastRefillTime = timestamp;
      return;
    }

    const elapsed = timestamp - this.lastRefillTime;
    const tokensToAdd = Math.floor(elapsed * this.refillRatePerSecond);

    this.tokens = Math.min(this.capacity, this.tokens + tokensToAdd);
    this.lastRefillTime += tokensToAdd / this.refillRatePerSecond;
  }

  tryConsume(timestamp: number): boolean {
    this.refillTokens(timestamp);
    this.updateLastUsed(timestamp);

    if(this.tokens >= 1) {
      this.tokens--;

      return true;
    } else {
      return false;
    }
  }
}

class RateLimiter {
  private defaultTokenBucketCapacity: number;
  private defaultRefillRatePerSecond: number;
  private inactivityLimit: number;
  private tokensByUsers: Map<string, TokenBucket> = new Map();

  constructor(defaultTokenBucketCapacity: number, defaultRefillRatePerSecond: number, inactivityLimit: number) {
    this.defaultTokenBucketCapacity = defaultTokenBucketCapacity;
    this.defaultRefillRatePerSecond = defaultRefillRatePerSecond;
    this.inactivityLimit = inactivityLimit;
  }

  cleanupOldTokenBuckets(timestamp: number): void {
    for(const [userId, bucket] of this.tokensByUsers) {
      if(timestamp - bucket.getLastUsed() > this.inactivityLimit) {
        this.tokensByUsers.delete(userId);
      }
    }
  }

  allowRequest(userId: string, timestamp: number): boolean {
    this.cleanupOldTokenBuckets(timestamp);
    
    let tokenBucket = this.tokensByUsers.get(userId);

    if(!tokenBucket) {
      tokenBucket = new TokenBucket(this.defaultTokenBucketCapacity, this.defaultRefillRatePerSecond);
    }

    if(tokenBucket.tryConsume(timestamp)) {
      this.tokensByUsers.set(userId, tokenBucket);
      
      return true;
    } else {
      return false;
    }
  }
}