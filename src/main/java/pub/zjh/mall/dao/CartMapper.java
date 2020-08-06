package pub.zjh.mall.dao;

import org.apache.ibatis.annotations.Param;
import pub.zjh.mall.pojo.Cart;

import java.util.Collection;
import java.util.List;

public interface CartMapper extends IBaseBatchOperation<Cart> {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Cart record);

    Cart selectByUserIdAndProductId(@Param("userId") Integer userId,
                                    @Param("productId") Integer productId);

    List<Cart> selectByUserId(Integer userId);

    int deleteByUserIdAndProductIds(@Param("userId") Integer userId,
                                    @Param("productIds") Collection productIds);

    int updateCheckedByUserId(@Param("userId") Integer userId,
                              @Param("productId") Integer productId,
                              @Param("checked") Integer checked);

    int selectCartProductCount(Integer userId);

}