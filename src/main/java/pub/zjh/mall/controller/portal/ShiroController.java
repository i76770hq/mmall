package pub.zjh.mall.controller.portal;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.zjh.mall.enums.ResponseEnum;
import pub.zjh.mall.vo.ResponseVo;

@RestController
public class ShiroController {

    @RequestMapping("/notLogin")
    public ResponseVo notLogin() {
        return ResponseVo.error(ResponseEnum.NEED_LOGIN);
    }

    @RequestMapping("/notRole")
    public ResponseVo notRole() {
        return ResponseVo.error(ResponseEnum.ERROR, ResponseEnum.NOT_ADMIN_LOGIN_ERROR.getDesc());
    }

}
