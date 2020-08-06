package pub.zjh.mall.dao;

import org.apache.ibatis.annotations.Param;
import pub.zjh.mall.pojo.OrderItem;

import java.util.Collection;
import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> selectByOrderNoAndUserId(@Param("orderNo") Long orderNo,
                                             @Param("userId") Integer userId);

    int batchInsert(@Param("orderItems") Collection<OrderItem> orderItems);

    List<OrderItem> selectByUserId(Integer userId);

    List<OrderItem> selectByOrderNo(@Param("orderNos") Collection<Long> orderNos);

}