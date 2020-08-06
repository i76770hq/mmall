package pub.zjh.mall.dao;

import org.apache.ibatis.annotations.Param;
import pub.zjh.mall.pojo.Shipping;

import java.util.Collection;
import java.util.List;

public interface ShippingMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Shipping record);

    int insertSelective(Shipping record);

    Shipping selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Shipping record);

    int updateByPrimaryKey(Shipping record);

    int deleteByShippingIdAndUserId(@Param("shippingId") Integer shippingId
            , @Param("userId") Integer userId);

    int updateByShippingIdAndUserId(Shipping shipping);

    Shipping selectByShippingIdAndUserId(@Param("shippingId") Integer shippingId
            , @Param("userId") Integer userId);

    List<Shipping> selectByUserId(Integer userId);

    List<Shipping> selectByShippingIds(@Param("shippingIds") Collection<Integer> shippingIds);

}