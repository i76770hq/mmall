package pub.zjh.mall.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
@Slf4j
public class RedisPoolUtil {

    @Autowired
    private JedisPool jedisPool;

    public Jedis getResource() {
        return jedisPool.getResource();
    }

    public void returnResource(Jedis jedis) {
        jedisPool.returnResource(jedis);
    }

    public void returnBrokenResource(Jedis jedis) {
        jedisPool.returnBrokenResource(jedis);
    }

    public void destroy() {
        jedisPool.destroy();
    }

    public String set(String key, String value) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = getResource();
            result = jedis.set(key, value);
        } catch (Exception e) {
            log.error("set key:{},value:{} error", key, value, e);
            returnBrokenResource(jedis);
        }
        returnResource(jedis);
        return result;
    }

    public String setEx(String key, String value, int expire) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = getResource();
            result = jedis.setex(key, expire, value);
        } catch (Exception e) {
            log.error("setex key:{},value:{} error", key, value, e);
            returnBrokenResource(jedis);
        }
        returnResource(jedis);
        return result;
    }

    public Long expire(String key, int expire) {
        Jedis jedis = null;
        Long result = null;
        try {
            jedis = getResource();
            result = jedis.expire(key, expire);
        } catch (Exception e) {
            log.error("expire key:{}, error", key, e);
            returnBrokenResource(jedis);
        }
        returnResource(jedis);
        return result;
    }

    public String get(String key) {
        Jedis jedis = null;
        String result = null;
        try {
            jedis = getResource();
            result = jedis.get(key);
        } catch (Exception e) {
            log.error("get key:{} error", key, e);
            returnBrokenResource(jedis);
        }
        returnResource(jedis);
        return result;
    }

    public Long del(String key) {
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = getResource();
            result = jedis.del(key);
        } catch (Exception e) {
            log.error("del key:{} error", key, e);
            returnBrokenResource(jedis);
        }
        returnResource(jedis);
        return result;
    }


}
