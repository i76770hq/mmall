package pub.zjh.mall.controller.portal;

import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.zjh.mall.form.ShippingForm;
import pub.zjh.mall.pojo.Shipping;
import pub.zjh.mall.pojo.User;
import pub.zjh.mall.service.IShippingService;
import pub.zjh.mall.vo.ResponseVo;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/shipping/")
@Validated
public class ShippingController {

    @Autowired
    private IShippingService shippingService;

    @RequestMapping("add.do")
    public ResponseVo add(User user,
                          ShippingForm shippingForm) {
        ResponseVo responseVo = shippingService.add(user.getId(), shippingForm);
        return responseVo;
    }

    @RequestMapping("del.do")
    public ResponseVo del(User user,
                          @NotNull Integer shippingId) {
        ResponseVo responseVo = shippingService.del(user.getId(), shippingId);
        return responseVo;
    }

    @RequestMapping("update.do")
    public ResponseVo update(User user,
                             ShippingForm shippingForm) {
        ResponseVo responseVo = shippingService.update(user.getId(), shippingForm);
        return responseVo;
    }

    @RequestMapping("select.do")
    public ResponseVo<Shipping> select(User user,
                                       @NotNull Integer shippingId) {
        ResponseVo responseVo = shippingService.select(user.getId(), shippingId);
        return responseVo;
    }

    @RequestMapping("list.do")
    public ResponseVo<PageInfo> list(User user,
                                     @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                     @RequestParam(value = "pageNum", defaultValue = "10") Integer pageSize) {
        ResponseVo responseVo = shippingService.list(user.getId(), pageNum, pageSize);
        return responseVo;
    }


}
