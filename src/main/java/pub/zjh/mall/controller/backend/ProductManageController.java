package pub.zjh.mall.controller.backend;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pub.zjh.mall.config.FtpConfig;
import pub.zjh.mall.consts.MallConst;
import pub.zjh.mall.form.ProductForm;
import pub.zjh.mall.pojo.User;
import pub.zjh.mall.service.IFileService;
import pub.zjh.mall.service.IProductService;
import pub.zjh.mall.service.IUserService;
import pub.zjh.mall.vo.ResponseVo;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/manage/product")
@Validated
@Slf4j
public class ProductManageController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IProductService productService;

    @Autowired
    private IFileService fileService;

    @Autowired
    private FtpConfig ftpConfig;

    @Autowired
    private ServletContext servletContext;

    /**
     * 后台新增OR更新商品
     *
     * @param user
     * @param productForm
     * @return
     */
    @RequestMapping("/save.do")
    public ResponseVo saveOrUpdateProduct(User user,
                                          @Valid ProductForm productForm) {
        userService.checkAdminRole(user);
        ResponseVo responseVo = productService.manageSaveOrUpdateProduct(productForm);
        return responseVo;
    }

    /**
     * 后台设置商品上下架状态
     *
     * @param user
     * @param productId
     * @param status
     * @return
     */
    @RequestMapping("/set_sale_status.do")
    public ResponseVo updateSaleStatus(User user,
                                       @NotNull Integer productId,
                                       @NotNull Integer status) {
        userService.checkAdminRole(user);
        ResponseVo responseVo = productService.manageUpdateSaleStatus(productId, status);
        return responseVo;
    }

    /**
     * 后台查询产品详情
     *
     * @param user
     * @param productId
     * @return
     */
    @RequestMapping("/detail.do")
    public ResponseVo getDetail(User user,
                                @NotNull Integer productId) {
        userService.checkAdminRole(user);
        ResponseVo responseVo = productService.getProductDetailByProductId(productId);
        return responseVo;
    }

    /**
     * 后台查询产品list
     *
     * @param user
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/list.do")
    public ResponseVo getDetail(User user,
                                @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        userService.checkAdminRole(user);
        ResponseVo responseVo = productService.manageGetAllProduct(pageNum, pageSize);
        return responseVo;
    }

    /**
     * 后台产品搜索
     *
     * @param user
     * @param productName
     * @param productId
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/search.do")
    public ResponseVo getDetail(User user,
                                String productName,
                                Integer productId,
                                @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
                                @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) {
        userService.checkAdminRole(user);
        ResponseVo responseVo = productService.manageGetProductByIdAndName(productName, productId, pageNum, pageSize);
        return responseVo;
    }

    /**
     * 后台图片上传
     *
     * @param user
     * @param multipartFile
     * @return
     * @throws IOException
     */
    @RequestMapping("/upload.do")
    public ResponseVo upload(User user,
                             @RequestParam(value = "upload_file", required = false) MultipartFile multipartFile) throws IOException {
        userService.checkAdminRole(user);
        String realUploadPath = servletContext.getRealPath(MallConst.IMAGE_UPLOAD_LOCAL_PATH);
        String fileName = fileService.uploadFTP(multipartFile, realUploadPath, MallConst.IMAGE_UPLOAD_LOCAL_PATH.replace(File.separator, "/"));
        String prefix = ftpConfig.getPrefix();
        String url = prefix + MallConst.IMAGE_UPLOAD_LOCAL_PATH.replace(File.separator, "/") + "/" + fileName;

        Map<String, String> map = Maps.newHashMap();
        map.put("url", url);
        map.put("uri", fileName);
        return ResponseVo.success(map);
    }

    /**
     * 后台富文本上传图片
     *
     * @param user
     * @param multipartFile
     * @return
     * @throws IOException
     */
    @RequestMapping("/richtext_img_upload.do")
    public Map richtextImgUpload(User user,
                                 @RequestParam(value = "upload_file", required = false) MultipartFile multipartFile,
                                 HttpServletResponse response) throws IOException {
        userService.checkAdminRole(user);
        String realUploadPath = servletContext.getRealPath(MallConst.IMAGE_UPLOAD_LOCAL_PATH);
        String fileName = fileService.uploadFTP(multipartFile, realUploadPath, MallConst.IMAGE_UPLOAD_LOCAL_PATH.replace(File.separator, "/"));
        String prefix = ftpConfig.getPrefix();
        String url = prefix + MallConst.IMAGE_UPLOAD_LOCAL_PATH.replace(File.separator, "/") + "/" + fileName;
        //富文本中对于返回值有自己的要求,我们使用是simditor所以按照simditor的要求进行返回
        //        {
        //            "success": true/false,
        //                "msg": "error message", # optional
        //            "file_path": "[real file path]"
        //        }
        Map<String, Object> resultMap = Maps.newHashMap();
        resultMap.put("success", true);
        resultMap.put("msg", "上传成功");
        resultMap.put("file_path", url);
        response.addHeader("Access-Control-Allow-Headers", "X-File-Name");
        return resultMap;
    }

}
