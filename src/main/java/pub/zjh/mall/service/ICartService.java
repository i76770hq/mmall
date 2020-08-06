package pub.zjh.mall.service;

import pub.zjh.mall.vo.CartVo;
import pub.zjh.mall.vo.ResponseVo;

public interface ICartService {

    /**
     * 购物车添加商品
     *
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    public ResponseVo<CartVo> add(Integer userId, Integer productId, Integer count);

    /**
     * 更新购物车商品数量
     *
     * @param userId
     * @param productId
     * @param count
     * @return
     */
    public ResponseVo<CartVo> update(Integer userId, Integer productId, Integer count);

    /**
     * 购物车删除商品
     *
     * @param userId
     * @param productIds
     * @return
     */
    public ResponseVo<CartVo> deleteProduct(Integer userId, String productIds);

    /**
     * 购物车List列表
     *
     * @param userId
     * @return
     */
    public ResponseVo<CartVo> list(Integer userId);

    public ResponseVo<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    /**
     * 查询购物车中产品的数量
     *
     * @param userId
     * @return
     */
    public ResponseVo<Integer> getCartProductCount(Integer userId);

}
