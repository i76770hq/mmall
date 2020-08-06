package pub.zjh.mall.dao;

import org.apache.ibatis.annotations.Param;
import pub.zjh.mall.pojo.Product;

import java.util.Collection;
import java.util.List;

public interface ProductMapper extends IBaseBatchOperation<Product> {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKey(Product record);

    List<Product> selectAllList();

    List<Product> selectByNameAndProductId(@Param("productName") String productName,
                                           @Param("productId") Integer productId);

    List<Product> selectByNameAndCategoryId(@Param("productName") String productName,
                                            @Param("categoryIds") List<Integer> categoryIds);

    List<Product> selectByIds(@Param("ids") Collection<Integer> ids);

}