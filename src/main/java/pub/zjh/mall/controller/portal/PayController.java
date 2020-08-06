package pub.zjh.mall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.zjh.mall.pojo.User;
import pub.zjh.mall.service.IOrderService;
import pub.zjh.mall.service.IPayService;
import pub.zjh.mall.vo.ResponseVo;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/order/")
@Validated
public class PayController {

    @Autowired
    private IPayService payService;


    @RequestMapping("pay.do")
    public ResponseVo pay(User user,
                          @NotNull Long orderNo,
                          HttpServletRequest request) {
        ResponseVo responseVo = payService.pay(orderNo, user.getId());
        return responseVo;
    }

    @RequestMapping("alipay_callback.do")
    public String alipayCallback(HttpServletRequest request) throws Exception {
        return payService.alipayCallback(request);
    }


}
