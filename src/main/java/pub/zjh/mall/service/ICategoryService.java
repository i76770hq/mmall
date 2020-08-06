package pub.zjh.mall.service;

import pub.zjh.mall.pojo.Category;
import pub.zjh.mall.vo.ResponseVo;

import java.util.List;

public interface ICategoryService {

    /**
     * 增加节点
     *
     * @param categoryName
     * @param parentId
     * @return
     */
    public ResponseVo addCategory(String categoryName, Integer parentId);

    /**
     * 修改品类名字
     *
     * @param categoryId
     * @param categoryName
     * @return
     */
    public ResponseVo updateCategoryName(Integer categoryId, String categoryName);

    /**
     * 查询子节点的category信息，并且不递归，保持平级
     *
     * @param categoryId
     * @return
     */
    public ResponseVo<List<Category>> getParallelChildrenCategory(Integer categoryId);

    /**
     * 查询当前节点的id和递归子节点的id
     *
     * @param categoryId
     * @return
     */
    public ResponseVo<List<Integer>> getDeepChildrenCategory(Integer categoryId);

}
