

## Step1 - introduce strategy pattern 
```text
                    +----------------------+
                    |     RateLimiter      |
                    +----------------------+
                    | - strategy           |
                    | : RateLimitStrategy  |
                    +----------------------+
                    | + allowRequest()     |
                    +----------+-----------+
                               |
                               |
                               v

                +-----------------------------+
                |    RateLimitStrategy         |
                +-----------------------------+
                | + allowRequest() : boolean  |
                +-------------+---------------+
                              ^
      +-----------+-----------+-----------+-----------+
      |           |           |           |           |
      |           |           |           |           |
+-----+-----+ +---+----+ +----+----+ +----+----+ +----+----+
|FixedWindow| |Token   | |Leaky    | |Sliding  | |Sliding  |
|Strategy   | |Bucket  | |Bucket   | |WindowLog| |Counter  |
+-----------+ +--------+ +---------+ +---------+ +---------+
```


## Step1 - introduce factory pattern - hide class creation

```text
                           +-------------------+
                           | RateLimiterFactory|
                           +-------------------+
                           | fixedWindow()     |
                           | tokenBucket()     |
                           | leakyBucket()     |
                           | slidingLog()      |
                           | slidingCounter()  |
                           +---------+---------+
                                     |
                                     |
                                     v

                           +-------------------+
                           |   RateLimiter     |
                           +-------------------+
                           | strategy          |
                           +-------------------+
```