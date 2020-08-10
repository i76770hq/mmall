package pub.zjh.mall.shiro;

import lombok.Data;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.util.SerializationUtils;
import pub.zjh.mall.util.RedisPoolUtil;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class RedisCache<K, V> implements Cache<K, V> {

    private String cacheName;

    private RedisPoolUtil redisPoolUtil;


    @Override
    public V get(K k) throws CacheException {
        byte[] value = redisPoolUtil.hget(cacheName.getBytes(), k.toString().getBytes());
        if (value == null) {
            return null;
        }
        return (V) SerializationUtils.deserialize(value);
    }

    @Override
    public V put(K k, V v) throws CacheException {
        byte[] serialize = SerializationUtils.serialize(v);
        redisPoolUtil.hset(cacheName.getBytes(), k.toString().getBytes(), serialize);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        redisPoolUtil.hdel(cacheName.getBytes(), k.toString().getBytes());
        return null;
    }

    @Override
    public void clear() throws CacheException {
        redisPoolUtil.del(cacheName);
    }

    @Override
    public int size() {
        return redisPoolUtil.hlen(cacheName.getBytes()).intValue();
    }

    @Override
    public Set<K> keys() {
        return (Set<K>) redisPoolUtil.hkeys(cacheName.getBytes()).stream().
                map(s -> new String(s)).
                collect(Collectors.toSet());
    }

    @Override
    public Collection<V> values() {
        return (List<V>) redisPoolUtil.hvals(cacheName.getBytes()).stream().
                map(s -> SerializationUtils.deserialize(s)).
                collect(Collectors.toList());
    }

}
