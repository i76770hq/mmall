package pub.zjh.mall.controller.backend;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pub.zjh.mall.pojo.User;
import pub.zjh.mall.service.ICategoryService;
import pub.zjh.mall.service.IUserService;
import pub.zjh.mall.vo.ResponseVo;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService userService;

    @Autowired
    private ICategoryService categoryService;

    /**
     * 增加节点
     *
     * @param sessionUser
     * @param categoryName
     * @param parentId
     * @return
     */
    @RequestMapping("/add_category.do")
    public ResponseVo addCategory(User sessionUser,
                                  @NotBlank String categoryName,
                                  @RequestParam(value = "parentId", defaultValue = "0") Integer parentId) {
        userService.checkAdminRole(sessionUser);
        ResponseVo responseVo = categoryService.addCategory(categoryName, parentId);
        return responseVo;
    }

    /**
     * 修改品类名字
     *
     * @param sessionUser
     * @param categoryId
     * @param categoryName
     * @return
     */
    @RequestMapping("/set_category_name.do")
    public ResponseVo updateCategoryName(User sessionUser,
                                         @NotNull Integer categoryId,
                                         @NotBlank String categoryName) {
        userService.checkAdminRole(sessionUser);
        ResponseVo responseVo = categoryService.updateCategoryName(categoryId, categoryName);
        return responseVo;
    }

    /**
     * 查询子节点的category信息，并且不递归，保持平级
     *
     * @param sessionUser
     * @param categoryId
     * @return
     */
    @RequestMapping("/get_category.do")
    public ResponseVo getParallelChildrenCategory(User sessionUser,
                                                  @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        userService.checkAdminRole(sessionUser);
        ResponseVo responseVo = categoryService.getParallelChildrenCategory(categoryId);
        return responseVo;
    }

    /**
     * 查询当前节点的id和递归子节点的id
     *
     * @param sessionUser
     * @param categoryId
     * @return
     */
    @RequestMapping("/get_deep_category.do")
    public ResponseVo getDeepChildrenCategory(User sessionUser,
                                              @RequestParam(value = "categoryId", defaultValue = "0") Integer categoryId) {
        userService.checkAdminRole(sessionUser);
        ResponseVo responseVo = categoryService.getDeepChildrenCategory(categoryId);
        return responseVo;
    }


}
