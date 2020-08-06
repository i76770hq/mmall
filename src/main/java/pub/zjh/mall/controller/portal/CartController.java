package pub.zjh.mall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pub.zjh.mall.consts.MallConst;
import pub.zjh.mall.pojo.User;
import pub.zjh.mall.service.ICartService;
import pub.zjh.mall.service.IUserService;
import pub.zjh.mall.vo.CartVo;
import pub.zjh.mall.vo.ResponseVo;

import javax.servlet.http.HttpSession;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/cart/")
@Validated
public class CartController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ICartService cartService;

    /**
     * 购物车List列表
     *
     * @param user
     * @return
     */
    @RequestMapping("list.do")
    public ResponseVo<CartVo> list(User user) {
        ResponseVo<CartVo> responseVo = cartService.list(user.getId());
        return responseVo;
    }

    /**
     * 购物车添加商品
     *
     * @param user
     * @param productId
     * @param count
     * @return
     */
    @RequestMapping("add.do")
    public ResponseVo<CartVo> add(User user,
                                  @NotNull Integer productId,
                                  @NotNull @Min(1) Integer count) {
        ResponseVo<CartVo> responseVo = cartService.add(user.getId(), productId, count);
        return responseVo;
    }

    @RequestMapping("update.do")
    public ResponseVo<CartVo> update(User user,
                                     @NotNull Integer productId,
                                     @NotNull @Min(1) Integer count) {
        ResponseVo<CartVo> responseVo = cartService.update(user.getId(), productId, count);
        return responseVo;
    }

    @RequestMapping("delete_product.do")
    public ResponseVo<CartVo> deleteProduct(User user,
                                            @NotBlank String productIds) {
        ResponseVo<CartVo> responseVo = cartService.deleteProduct(user.getId(), productIds);
        return responseVo;
    }


    @RequestMapping("select_all.do")
    public ResponseVo<CartVo> selectAll(User user) {
        ResponseVo<CartVo> responseVo = cartService.selectOrUnSelect(user.getId(), null, MallConst.CHECKED);
        return responseVo;
    }

    @RequestMapping("un_select_all.do")
    public ResponseVo<CartVo> unSelectAll(User user) {
        ResponseVo<CartVo> responseVo = cartService.selectOrUnSelect(user.getId(), null, MallConst.UN_CHECKED);
        return responseVo;
    }

    @RequestMapping("select.do")
    public ResponseVo<CartVo> select(User user, @NotNull Integer productId) {
        ResponseVo<CartVo> responseVo = cartService.selectOrUnSelect(user.getId(), productId, MallConst.CHECKED);
        return responseVo;
    }

    @RequestMapping("un_select.do")
    public ResponseVo<CartVo> unSelect(User user, @NotNull Integer productId) {
        ResponseVo<CartVo> responseVo = cartService.selectOrUnSelect(user.getId(), productId, MallConst.UN_CHECKED);
        return responseVo;
    }

    @RequestMapping("get_cart_product_count.do")
    public ResponseVo<Integer> getCartProductCount(User user, HttpSession session) {
//        User user = (User) session.getAttribute(MallConst.CURRENT_USER);
        if (user == null) {
            return ResponseVo.success(0);
        }
        ResponseVo<Integer> responseVo = cartService.getCartProductCount(user.getId());
        return responseVo;
    }

}
