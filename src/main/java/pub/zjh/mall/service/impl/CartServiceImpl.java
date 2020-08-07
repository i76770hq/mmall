package pub.zjh.mall.service.impl;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pub.zjh.mall.config.FtpConfig;
import pub.zjh.mall.consts.MallConst;
import pub.zjh.mall.dao.CartMapper;
import pub.zjh.mall.dao.ProductMapper;
import pub.zjh.mall.pojo.Cart;
import pub.zjh.mall.pojo.Product;
import pub.zjh.mall.service.ICartService;
import pub.zjh.mall.util.BigDecimalUtil;
import pub.zjh.mall.vo.CartProductVo;
import pub.zjh.mall.vo.CartVo;
import pub.zjh.mall.vo.ResponseVo;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CartServiceImpl implements ICartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private FtpConfig ftpConfig;

    @Autowired
    private BatchOperationService batchOperationService;

    @Override
    public ResponseVo<CartVo> add(Integer userId, Integer productId, Integer count) {

        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart == null) {
            Cart insertCart = new Cart();
            insertCart.setProductId(productId);
            insertCart.setUserId(userId);
            insertCart.setQuantity(count);
            insertCart.setChecked(MallConst.CHECKED);
            cartMapper.insert(insertCart);
        } else {
            Integer quantity = cart.getQuantity();
            cart.setQuantity(quantity + count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return list(userId);
    }

    @Override
    public ResponseVo<CartVo> update(Integer userId, Integer productId, Integer count) {
        Cart cart = cartMapper.selectByUserIdAndProductId(userId, productId);
        if (cart != null) {
            cart.setQuantity(count);
            cartMapper.updateByPrimaryKeySelective(cart);
        }
        return list(userId);
    }

    @Override
    public ResponseVo<CartVo> deleteProduct(Integer userId, String productIds) {
        List<String> productIdList = Splitter.on(",").splitToList(productIds);
        if (!CollectionUtils.isEmpty(productIdList)) {
            cartMapper.deleteByUserIdAndProductIds(userId, productIdList);
        }
        return list(userId);
    }

    @Override
    public ResponseVo<CartVo> list(Integer userId) {
        CartVo cartVo = getCartVo(userId);
        return ResponseVo.success(cartVo);
    }

    @Override
    public ResponseVo<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked) {
        cartMapper.updateCheckedByUserId(userId, productId, checked);
        return list(userId);
    }

    @Override
    public ResponseVo<Integer> getCartProductCount(Integer userId) {
        if (userId == null) {
            return ResponseVo.success(0);
        }
        int count = cartMapper.selectCartProductCount(userId);
        return ResponseVo.success(count);
    }


    public CartVo getCartVo(Integer userId) {
        List<Cart> cartList = cartMapper.selectByUserId(userId);

        List<Integer> productIdList = cartList.stream()
                .map(cart -> cart.getProductId())
                .collect(Collectors.toList());

        List<Product> productList = productIdList.isEmpty() ? Lists.newArrayList() : productMapper.selectByIds(productIdList);
        Map<Integer, Product> productMap = productList.stream()
                .collect(Collectors.toMap(Product::getId, Function.identity()));

        List<CartProductVo> cartProductVoList = Lists.newArrayList();

        boolean allChecked = true;
        BigDecimal cartTotalPrice = BigDecimal.ZERO;

        List<Cart> updateCartList = Lists.newArrayList();

        for (Cart cart : cartList) {
            Product product = productMap.get(cart.getProductId());
            if (product == null) {
                continue;
            }
            CartProductVo cartProductVo = new CartProductVo();
            BeanUtils.copyProperties(cart, cartProductVo);
            cartProductVo.setProductName(product.getName());
            cartProductVo.setProductSubtitle(product.getSubtitle());
            cartProductVo.setProductMainImage(product.getMainImage());
            cartProductVo.setProductPrice(product.getPrice());
            cartProductVo.setProductStatus(product.getStatus());
            cartProductVo.setProductStock(product.getStock());
            cartProductVo.setProductChecked(cart.getChecked());

            String limitQuantity = MallConst.LIMIT_NUM_SUCCESS;
            if (cart.getQuantity() > product.getStock()) {
                cartProductVo.setQuantity(product.getStock());
                limitQuantity = MallConst.LIMIT_NUM_FAIL;

                Cart updateCart = new Cart();
                updateCart.setId(cart.getId());
                updateCart.setQuantity(product.getStock());
                if (cart.getQuantity() != product.getStock()) {
                    updateCartList.add(updateCart);
                }
            }

            cartProductVo.setProductTotalPrice(BigDecimalUtil.mul(product.getPrice().doubleValue(), cartProductVo.getQuantity()));

            cartProductVo.setLimitQuantity(limitQuantity);

            if (cart.getChecked() == MallConst.UN_CHECKED) {
                allChecked = false;
            } else {
                cartTotalPrice = BigDecimalUtil.add(cartTotalPrice.doubleValue(), cartProductVo.getProductTotalPrice().doubleValue());
            }
            cartProductVoList.add(cartProductVo);
        }

        //批量更新购物车表中的商品数量
        batchOperationService.batchUpdate(updateCartList, CartMapper.class);

        CartVo cartVo = new CartVo();
        cartVo.setCartProductVoList(cartProductVoList);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setAllChecked(allChecked);
        cartVo.setImageHost(ftpConfig.getPrefix() + MallConst.IMAGE_UPLOAD_LOCAL_PATH.replace(File.separator, "/") + "/");
        return cartVo;
    }

}
