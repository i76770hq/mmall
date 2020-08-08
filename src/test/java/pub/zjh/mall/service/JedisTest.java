package pub.zjh.mall.service;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pub.zjh.mall.MallApplicationTests;
import pub.zjh.mall.util.RedisPoolUtil;

public class JedisTest extends MallApplicationTests {

    @Autowired
    private RedisPoolUtil redisPoolUtil;

    @Test
    public void test() {
        redisPoolUtil.set("name", "zjh");
        String name = redisPoolUtil.get("name");

        redisPoolUtil.setEx("keyex", "valueex", 10 * 60);
        redisPoolUtil.expire("name", 20 * 60);

        redisPoolUtil.del("name");

        redisPoolUtil.destroy();
        System.out.println("end----------");

    }


}
