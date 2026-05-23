import java.util.*;import java.util.concurrent.*;
enum Decision{ALLOW,REJECT}
class TokenBucket{int capacity,tokens,refill;long last=System.nanoTime();TokenBucket(int capacity,int refill){this.capacity=capacity;this.tokens=capacity;this.refill=refill;}synchronized Decision allow(){long now=System.nanoTime();long seconds=(now-last)/1_000_000_000L;if(seconds>0){tokens=Math.min(capacity,tokens+(int)seconds*refill);last=now;}if(tokens<=0)return Decision.REJECT;tokens--;return Decision.ALLOW;}}
class RateLimiter{Map<String,TokenBucket>buckets=new ConcurrentHashMap<>();Decision allow(String key){return buckets.computeIfAbsent(key,k->new TokenBucket(2,1)).allow();}}
public class ApiGatewayThrottlingClient{public static void main(String[]args){RateLimiter limiter=new RateLimiter();System.out.println(limiter.allow("client"));System.out.println(limiter.allow("client"));System.out.println(limiter.allow("client"));}}
