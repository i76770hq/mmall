package pub.zjh.mall.controller.portal;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.zjh.mall.pojo.User;
import pub.zjh.mall.service.IOrderService;
import pub.zjh.mall.vo.OrderVo;
import pub.zjh.mall.vo.ResponseVo;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/order/")
@Validated
public class OrderController {

    @Autowired
    private IOrderService orderService;


    @RequestMapping("query_order_pay_status.do")
    public ResponseVo<Boolean> queryOrderPayStatus(User user, @NotNull Long orderNo) {
        ResponseVo<Boolean> responseVo = orderService.selectByOrderNoAndUserId(orderNo, user.getId());
        return responseVo;
    }

    @RequestMapping("create.do")
    public ResponseVo<OrderVo> create(User user, @NotNull Integer shippingId) {
        ResponseVo<OrderVo> responseVo = orderService.create(user.getId(), shippingId);
        return responseVo;
    }

    @RequestMapping("cancel.do")
    public ResponseVo cancel(User user, @NotNull Long orderNo) {
        ResponseVo responseVo = orderService.cancel(orderNo, user.getId());
        return responseVo;
    }

    @RequestMapping("get_order_cart_product.do")
    public ResponseVo getOrderCartProduct(User user) {
        ResponseVo responseVo = orderService.getOrderCartProduct(user.getId());
        return responseVo;
    }

    @RequestMapping("detail.do")
    public ResponseVo detail(User user, @NotNull Long orderNo) {
        ResponseVo responseVo = orderService.getOrderDetail(user.getId(), orderNo);
        return responseVo;
    }

    @RequestMapping("list.do")
    public ResponseVo<PageInfo> list(User user,
                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        ResponseVo<PageInfo> responseVo = orderService.list(user.getId(), pageNum, pageSize);
        return responseVo;
    }

}
