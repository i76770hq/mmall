package pub.zjh.mall.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

@ConfigurationProperties(prefix = "redis")
@Configuration
@Data
public class RedisConfig {

    private String ip;

    private Integer port;

    private String password;

    private Integer timeout;

    /**
     * 最大连接数
     */
    private Integer maxTotal;
    /**
     * 最大空闲数
     */
    private Integer maxIdle;
    /**
     * 最小空闲数
     */
    private Integer minIdle;

    /**
     * 从jedis连接池获取连接时，校验并返回可用的连接
     */
    private Boolean testOnBorrow;
    /**
     * 把连接放回jedis连接池时，校验并返回可用的连接
     */
    private Boolean testOnReturn;

    /**
     * 连接耗尽的时候,是否阻塞,false会抛出异常,true阻塞直到超时,默认为true
     */
    private Boolean blockWhenExhausted;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(maxTotal);
        jedisPoolConfig.setMaxIdle(maxIdle);
        jedisPoolConfig.setMinIdle(minIdle);
        jedisPoolConfig.setTestOnBorrow(testOnBorrow);
        jedisPoolConfig.setTestOnReturn(testOnReturn);
        jedisPoolConfig.setBlockWhenExhausted(blockWhenExhausted);
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, ip
                , port, timeout, password);
        return jedisPool;
    }

}
