package pub.zjh.mall.service;

import com.github.pagehelper.PageInfo;
import pub.zjh.mall.form.ProductForm;
import pub.zjh.mall.vo.ProductDetailVo;
import pub.zjh.mall.vo.ResponseVo;

public interface IProductService {

    /**
     * 后台新增或更新产品
     *
     * @param productForm
     * @return
     */
    public ResponseVo manageSaveOrUpdateProduct(ProductForm productForm);

    /**
     * 后台设置商品上下架状态
     *
     * @param productId
     * @param status
     * @return
     */
    public ResponseVo manageUpdateSaleStatus(Integer productId, Integer status);

    /**
     * 后台查询产品详情
     *
     * @param productId
     * @return
     */
    public ResponseVo<ProductDetailVo> manageProductDetailById(Integer productId);

    /**
     * 后台查询产品list
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ResponseVo<PageInfo> manageGetAllProduct(Integer pageNum, Integer pageSize);


    /**
     * 后台搜索产品
     *
     * @param name
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ResponseVo<PageInfo> manageGetProductByIdAndName(String name,
                                                            Integer productId,
                                                            Integer pageNum,
                                                            Integer pageSize);

    /**
     * 前台查询商品详情
     *
     * @param productId
     * @return
     */
    public ResponseVo<ProductDetailVo> getProductDetailByProductId(Integer productId);


    /**
     * 前台产品搜索及动态排序List
     *
     * @param productName
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @return
     */
    public ResponseVo<PageInfo> getProductByNameAndCategoryId(String productName,
                                                              Integer categoryId,
                                                              Integer pageNum,
                                                              Integer pageSize,
                                                              String orderBy);

}
