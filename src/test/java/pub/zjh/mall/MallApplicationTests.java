package pub.zjh.mall;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import pub.zjh.mall.config.MD5Config;
import pub.zjh.mall.dao.UserMapper;
import pub.zjh.mall.pojo.User;

@RunWith(SpringRunner.class)
@SpringBootTest
public class MallApplicationTests {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void contextLoads() {
        User user = userMapper.selectByUsername("admin");
        System.out.println();
    }

}
