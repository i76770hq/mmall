package pub.zjh.mall.shiro;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.apache.shiro.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pub.zjh.mall.util.RedisPoolUtil;

/**
 * 自定义Redis缓存管理器
 */
@Component
public class RedisCacheManager implements CacheManager {

    @Autowired
    private RedisPoolUtil redisPoolUtill;

    @Override
    public <K, V> Cache<K, V> getCache(String cacheName) throws CacheException {
        RedisCache<K, V> redisCache = new RedisCache<>();
        redisCache.setCacheName(cacheName);
        redisCache.setRedisPoolUtil(redisPoolUtill);
        return redisCache;
    }

}
