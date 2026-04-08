import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TimeWindowRateLimiter implements RateLimiterStrategy{
    private int limit;
    private int timeWindow;
    private Map<String, List<Integer>> requestsByUser = new HashMap<>();

    public TimeWindowRateLimiter(int limit, int timeWindow) {
        this.limit = limit;
        this.timeWindow = timeWindow;
    }

    public void removeOldRequests(List<Integer> requests, int timestamp) {
        while(!requests.isEmpty() && requests.getFirst() <= timestamp - this.timeWindow) {
            requests.removeFirst();
        }
    }
    public void cleanUpRequestsByUser(int timestamp) {
        this.requestsByUser.forEach((userId, requests) -> {
            this.removeOldRequests(requests, timestamp);

            if(requests.isEmpty()) {
                this.requestsByUser.remove(userId);
            }
        });
    }

    public boolean allowRequest(String userId, int timestamp) {
        List<Integer> requests = this.requestsByUser.get(userId);
        if(requests == null) requests = new ArrayList<>();

        this.removeOldRequests(requests, timestamp);

        if(requests.size() >= this.limit) {
            return false;
        }

        requests.add(timestamp);
        this.requestsByUser.put(userId, requests);

        return true;
    }
}
