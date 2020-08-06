package pub.zjh.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pub.zjh.mall.config.FtpConfig;
import pub.zjh.mall.consts.MallConst;
import pub.zjh.mall.dao.CategoryMapper;
import pub.zjh.mall.dao.ProductMapper;
import pub.zjh.mall.enums.ProductStatusEnum;
import pub.zjh.mall.enums.ResponseEnum;
import pub.zjh.mall.exception.MallException;
import pub.zjh.mall.form.ProductForm;
import pub.zjh.mall.pojo.Category;
import pub.zjh.mall.pojo.Product;
import pub.zjh.mall.service.ICategoryService;
import pub.zjh.mall.service.IProductService;
import pub.zjh.mall.vo.ProductDetailVo;
import pub.zjh.mall.vo.ProductListVo;
import pub.zjh.mall.vo.ResponseVo;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private FtpConfig ftpConfig;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService categoryService;

    /**
     * 新增或更新产品
     *
     * @param productForm
     * @return
     */
    @Override
    public ResponseVo manageSaveOrUpdateProduct(ProductForm productForm) {
        if (StringUtils.isNotBlank(productForm.getSubImages())) {
            String[] subImagesArray = productForm.getSubImages().split(",");
            if (subImagesArray.length > 0) {
                productForm.setMainImage(subImagesArray[0]);
            }
        }
        Product product = new Product();
        BeanUtils.copyProperties(productForm, product);

        if (productForm.getId() == null) {
            int count = productMapper.insert(product);
            if (count <= 0) {
                throw new MallException(ResponseEnum.ADD_PRODUCT_ERROR.getDesc());
            }
        } else {
            int count = productMapper.updateByPrimaryKey(product);
            if (count <= 0) {
                throw new MallException(ResponseEnum.UPDATE_PRODUCT_ERROR.getDesc());
            }
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo manageUpdateSaleStatus(Integer productId, Integer status) {
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int count = productMapper.updateByPrimaryKeySelective(product);
        if (count <= 0) {
            throw new MallException(ResponseEnum.UPDATE_PRODUCT_STATUS_ERROR.getDesc());
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo<ProductDetailVo> manageProductDetailById(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            throw new MallException(ResponseEnum.PRODUCT_NOT_EXIST.getDesc());
        }
        if (product.getStatus() != ProductStatusEnum.ON_SALE.getCode()) {
            throw new MallException(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE.getDesc());
        }
        ProductDetailVo productDetailVo = product2ProductDetailVoConverter(product);
        return ResponseVo.success(productDetailVo);
    }

    @Override
    public ResponseVo<PageInfo> manageGetAllProduct(Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectAllList();
        List<ProductListVo> productListVoList = productList.stream()
                .map(product -> product2ProductListVoConverter(product))
                .collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<PageInfo> manageGetProductByIdAndName(String name, Integer productId, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        if (StringUtils.isNotBlank(name)) {
            name = "%" + name + "%";
        }
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectByNameAndProductId(name, productId);
        List<ProductListVo> productListVoList = productList.stream()
                .map(product -> product2ProductListVoConverter(product))
                .collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<ProductDetailVo> getProductDetailByProductId(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product == null) {
            throw new MallException(ResponseEnum.PRODUCT_NOT_EXIST.getDesc());
        }
        if (product.getStatus() != ProductStatusEnum.ON_SALE.getCode()) {
            throw new MallException(ResponseEnum.PRODUCT_OFF_SALE_OR_DELETE.getDesc());
        }
        ProductDetailVo productDetailVo = product2ProductDetailVoConverter(product);
        return ResponseVo.success(productDetailVo);
    }

    @Override
    public ResponseVo<PageInfo> getProductByNameAndCategoryId(String productName, Integer categoryId, Integer pageNum, Integer pageSize, String orderBy) {
        if (StringUtils.isBlank(productName)) {
            productName = null;
        } else {
            productName = "%" + productName + "%";
        }

        List<Integer> categoryList = Lists.newArrayList();
        if (categoryId != null) {
            ResponseVo<List<Integer>> deepChildrenCategory = categoryService.getDeepChildrenCategory(categoryId);
            categoryList = deepChildrenCategory.getData();
        }

        PageHelper.startPage(pageNum, pageSize);
        //排序处理
        if (StringUtils.isNotBlank(orderBy)) {
            if (MallConst.PRICE_ASC_DESC.contains(orderBy)) {
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
            }
        }

        List<Product> productList = productMapper.selectByNameAndCategoryId(productName, categoryList);

        List<ProductListVo> productListVoList = productList.stream()
                .map(product -> product2ProductListVoConverter(product))
                .collect(Collectors.toList());

        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return ResponseVo.success(pageInfo);
    }

    private ProductListVo product2ProductListVoConverter(Product product) {
        ProductListVo productListVo = new ProductListVo();
        BeanUtils.copyProperties(product, productListVo);
        String prefix = ftpConfig.getPrefix();
        productListVo.setImageHost(prefix + MallConst.IMAGE_UPLOAD_LOCAL_PATH.replace(File.separator, "/") + "/");
        return productListVo;
    }

    private ProductDetailVo product2ProductDetailVoConverter(Product product) {
        ProductDetailVo productDetailVo = new ProductDetailVo();
        BeanUtils.copyProperties(product, productDetailVo);

        String prefix = ftpConfig.getPrefix();
        productDetailVo.setImageHost(prefix + MallConst.IMAGE_UPLOAD_LOCAL_PATH.replace(File.separator, "/") + "/");
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if (category == null) {
            productDetailVo.setParentCategoryId(0);
        } else {
            productDetailVo.setParentCategoryId(category.getParentId());
        }
        return productDetailVo;
    }

}
