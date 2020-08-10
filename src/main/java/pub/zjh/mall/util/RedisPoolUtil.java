package pub.zjh.mall.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class RedisPoolUtil {

    @Autowired
    private JedisPool jedisPool;

    public void returnResource(Jedis jedis) {
        jedisPool.returnResource(jedis);
    }

    public Jedis getResource() {
        return jedisPool.getResource();
    }

    public void returnBrokenResource(Jedis jedis) {
        jedisPool.returnBrokenResource(jedis);
    }

    public void destroy() {
        jedisPool.destroy();
    }

    public Set<byte[]> keys(byte[] key) {
        Jedis jedis = null;
        Set<byte[]> result = null;
        try {
            jedis = getResource();
            result = jedis.keys(key);
        } catch (Exception e) {
            log.error("keys key:{} error", key, e);
            returnBrokenResource(jedis);
        }
        returnResource(jedis);
        return result;
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

    public String set(byte[] key, byte[] value) {
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

    public Long expire(byte[] key, int expire) {
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

    public byte[] get(byte[] key) {
        Jedis jedis = null;
        byte[] result = null;
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

    public Long del(byte[] key) {
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

    public byte[] hget(byte[] key, byte[] hashKey) {
        Jedis jedis = null;
        byte[] result = null;

        try {
            jedis = getResource();
            result = jedis.hget(key, hashKey);
        } catch (Exception e) {
            log.error("hget key:{} hashKey:{} error", key, hashKey, e);
            returnBrokenResource(jedis);
        }
        returnResource(jedis);
        return result;
    }

    public String hmset(byte[] key, Map<byte[], byte[]> hash) {
        Jedis jedis = null;
        String result = null;

        try {
            jedis = getResource();
            result = jedis.hmset(key, hash);
        } catch (Exception e) {
            log.error("hget key:{} hashMap:{} error", key, JsonUtil.obj2String(hash), e);
            returnBrokenResource(jedis);
        }
        returnResource(jedis);
        return result;
    }

    public Long hset(byte[] key, byte[] hashKey, byte[] hashValue) {
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = getResource();
            result = jedis.hset(key, hashKey, hashValue);
        } catch (Exception e) {
            log.error("hget key:{} hashKey:{} hashValue:{} error", key, hashKey, hashValue, e);
            returnBrokenResource(jedis);
        }
        returnResource(jedis);
        return result;
    }

    public Long hdel(byte[] key, byte[]... field) {
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = getResource();
            result = jedis.hdel(key, field);
        } catch (Exception e) {
            log.error("hdel key:{} field:{} error", key, JsonUtil.obj2String(field), e);
            returnBrokenResource(jedis);
        }
        returnResource(jedis);
        return result;
    }

    public Long hlen(byte[] key) {
        Jedis jedis = null;
        Long result = null;

        try {
            jedis = getResource();
            result = jedis.hlen(key);
        } catch (Exception e) {
            log.error("hlen key:{} error", key, e);
            returnBrokenResource(jedis);
        }
        returnResource(jedis);
        return result;
    }

    public Set<byte[]> hkeys(byte[] key) {
        Jedis jedis = null;
        Set<byte[]> result = null;

        try {
            jedis = getResource();
            result = jedis.hkeys(key);
        } catch (Exception e) {
            log.error("hkeys key:{} error", key, e);
            returnBrokenResource(jedis);
        }
        returnResource(jedis);
        return result;
    }

    public List<byte[]> hvals(byte[] key) {
        Jedis jedis = null;
        List<byte[]> result = null;

        try {
            jedis = getResource();
            result = jedis.hvals(key);
        } catch (Exception e) {
            log.error("hvals key:{} error", key, e);
            returnBrokenResource(jedis);
        }
        returnResource(jedis);
        return result;
    }

}
