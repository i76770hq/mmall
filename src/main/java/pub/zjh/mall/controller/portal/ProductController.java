package pub.zjh.mall.controller.portal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.zjh.mall.service.IProductService;
import pub.zjh.mall.vo.ResponseVo;

import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/product/")
public class ProductController {

    @Autowired
    private IProductService productService;

    /**
     * 前台查询产品详情
     *
     * @param productId
     * @return
     */
    @RequestMapping("detail.do")
    public ResponseVo getDetail(@NotNull Integer productId) {
        ResponseVo responseVo = productService.manageProductDetailById(productId);
        return responseVo;
    }

    /**
     * 前台产品搜索及动态排序List
     *
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("list.do")
    public ResponseVo list(@RequestParam(value = "keyword", required = false) String keyword,
                           @RequestParam(value = "categoryId", required = false) Integer categoryId,
                           @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                           @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
                           @RequestParam(value = "orderBy", defaultValue = "") String orderBy) {
        ResponseVo responseVo = productService.getProductByNameAndCategoryId(keyword, categoryId, pageNum, pageSize, orderBy);
        return responseVo;
    }

}
