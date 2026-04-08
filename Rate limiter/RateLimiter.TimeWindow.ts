class RateLimiter {
  private limit: number;
  private timeWindow: number;
  private requestTimestamps: Map<string, number[]>;

  constructor(limit: number, timeWindow: number) {
    this.limit = limit;
    this.timeWindow = timeWindow;
    this.requestTimestamps = new Map();
  }

  allowRequest(userId: string, timestamp: number): boolean {    
    const requestsByUser = this.requestTimestamps.get(userId) || [];

    while(requestsByUser.length > 0 && requestsByUser[0] <= timestamp - this.timeWindow) {
      requestsByUser.shift();
    }

    if(requestsByUser.length >= this.limit) return false;

    requestsByUser.push(timestamp);
    this.requestTimestamps.set(userId, requestsByUser);

    return true;
  }
}

// Usage
const rateLimiter = new RateLimiter(3, 10);
const userId1 = 'userId1';
const userId2 = 'userId2';
rateLimiter.allowRequest(userId1, 1); // Should return true
rateLimiter.allowRequest(userId1, 2); // Should return true
rateLimiter.allowRequest(userId1, 3); // Should return true
rateLimiter.allowRequest(userId1, 4); // Should return false
rateLimiter.allowRequest(userId2, 5); // Should return true
rateLimiter.allowRequest(userId1, 15); // Should return true