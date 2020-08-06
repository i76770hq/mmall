package pub.zjh.mall.dao;

import pub.zjh.mall.pojo.Cart;

public interface IBaseBatchOperation<T> {

    int updateByPrimaryKeySelective(T record);

}
