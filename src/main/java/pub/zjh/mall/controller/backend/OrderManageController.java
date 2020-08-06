package pub.zjh.mall.controller.backend;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.zjh.mall.pojo.User;
import pub.zjh.mall.service.IOrderService;
import pub.zjh.mall.service.IUserService;
import pub.zjh.mall.vo.OrderVo;
import pub.zjh.mall.vo.ResponseVo;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/manage/order/")
public class OrderManageController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IOrderService orderService;

    @RequestMapping("list.do")
    public ResponseVo<PageInfo> list(User user,
                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        userService.checkAdminRole(user);
        ResponseVo<PageInfo> responseVo = orderService.managelist(pageNum, pageSize);
        return responseVo;
    }

    @RequestMapping("detail.do")
    public ResponseVo<OrderVo> orderDetail(User user,
                                           @NotNull Long orderNo) {
        userService.checkAdminRole(user);
        ResponseVo<OrderVo> responseVo = orderService.manageGetOrderDetail(orderNo);
        return responseVo;
    }

    @RequestMapping("search.do")
    public ResponseVo<PageInfo> search(User user,
                                       @NotNull Long orderNo,
                                       @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                       @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        userService.checkAdminRole(user);
        ResponseVo<PageInfo> responseVo = orderService.manageSearch(orderNo, pageNum, pageSize);
        return responseVo;
    }

    @RequestMapping("send_goods.do")
    public ResponseVo<String> sendGoods(User user, @NotNull Long orderNo) {
        userService.checkAdminRole(user);
        ResponseVo<String> responseVo = orderService.manageSendGoods(orderNo);
        return responseVo;
    }

}
