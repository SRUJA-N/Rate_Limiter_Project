local tokenKey = KEYS[1]
local refillKey = KEYS[2]

local capacity = tonumber(ARGV[1])
local refillRate = tonumber(ARGV[2])
local currentTime = tonumber(ARGV[3])
local ttl = tonumber(ARGV[4])

local token = tonumber(redis.call("GET", tokenKey))
local lastRefill = tonumber(redis.call("GET", refillKey))

-- First request: create a full bucket
if token == nil then
    token = capacity
    lastRefill = currentTime
end

-- Calculate elapsed time
local elapsed = currentTime - lastRefill

-- Calculate how many tokens should be added
local tokensToAdd = math.floor(elapsed * refillRate / 1000)

if tokensToAdd > 0 then

    token = math.min(capacity, token + tokensToAdd)

    -- Keep the unused fraction of elapsed time
    local timeUsed = tokensToAdd * 1000 / refillRate
    lastRefill = lastRefill + timeUsed
end

-- No token available
if token <= 0 then

    local refillInterval = 1000 / refillRate
    local timeUntilNextToken =
        refillInterval - (currentTime - lastRefill)

    local retryAfter =
        math.max(1, math.ceil(timeUntilNextToken / 1000))

    return {0, 0, retryAfter}
end

-- Consume one token
token = token - 1

-- Save state
redis.call("SET", tokenKey, token)
redis.call("SET", refillKey, lastRefill)

redis.call("EXPIRE", tokenKey, ttl)
redis.call("EXPIRE", refillKey, ttl)

-- Accepted
return {1, token, 0}