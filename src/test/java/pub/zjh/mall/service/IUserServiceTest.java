package pub.zjh.mall.service;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pub.zjh.mall.MallApplicationTests;
import pub.zjh.mall.pojo.User;
import pub.zjh.mall.vo.ResponseVo;

@Slf4j
public class IUserServiceTest extends MallApplicationTests {

    @Autowired
    private IUserService userService;

    public static final String username = "admin";

    public static final String password = "admin";

    @Test
    public void login() {
        ResponseVo<User> login = userService.login(username, password);
        log.info("{}", JSON.toJSONString(login, true));
    }

}