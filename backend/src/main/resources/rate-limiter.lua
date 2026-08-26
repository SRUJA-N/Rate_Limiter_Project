local tokenKey=KEYS[1]
local refillKey=KEYS[2]
local capacity=tonumber(ARGV[1])
local refillRate=tonumber(ARGV[2])
local currentTime=tonumber(ARGV[3])
local ttl=tonumber(ARGV[4])
local token=tonumber(redis.call("GET",tokenKey))
local lastRefill=tonumber(redis.call("GET",refillKey))


if token==nil then
    token=capacity
    lastRefill=currentTime
end


local time=currentTime-lastRefill
local timepassed=math.floor(time/1000)
local tokenToAdd=timepassed*refillRate

if tokenToAdd>0 then
    token=math.min(capacity,tokenToAdd+token)
    lastRefill=currentTime
end

if token<=0 then
    return 0
end

token=token-1

redis.call("SET",tokenKey,token)
redis.call("SET",refillKey,lastRefill)
redis.call("EXPIRE",tokenKey,ttl)
redis.call("EXPIRE",refillKey,ttl)

return 1

