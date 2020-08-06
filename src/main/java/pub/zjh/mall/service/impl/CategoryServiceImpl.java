package pub.zjh.mall.service.impl;

import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import pub.zjh.mall.consts.MallConst;
import pub.zjh.mall.dao.CategoryMapper;
import pub.zjh.mall.enums.ResponseEnum;
import pub.zjh.mall.exception.MallException;
import pub.zjh.mall.pojo.Category;
import pub.zjh.mall.service.ICategoryService;
import pub.zjh.mall.vo.ResponseVo;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CategoryServiceImpl implements ICategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public ResponseVo addCategory(String categoryName, Integer parentId) {
        Category category = new Category();
        category.setName(categoryName);
        category.setParentId(parentId);
        category.setStatus(true);
        int count = categoryMapper.insert(category);
        if (count <= 0) {
            throw new MallException(ResponseEnum.ADD_CATEGORY_ERROR.getDesc());
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo updateCategoryName(Integer categoryId, String categoryName) {

        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);
        int count = categoryMapper.updateByPrimaryKeySelective(category);
        if (count <= 0) {
            throw new MallException(ResponseEnum.UPDATE_CATEGORY_NAME_ERROR.getDesc());
        }
        return ResponseVo.success();
    }

    @Override
    public ResponseVo<List<Category>> getParallelChildrenCategory(Integer categoryId) {
        List<Category> categoryList = categoryMapper.selectCategoryChildrenByParentId(categoryId);
        if (CollectionUtils.isEmpty(categoryList)) {
            log.error("{}", "未找到当前分类的子分类");
        }
        return ResponseVo.success(categoryList);
    }

    @Override
    public ResponseVo<List<Integer>> getDeepChildrenCategory(Integer categoryId) {
        List<Category> allCategoryList = categoryMapper.selectAll();
        Set<Integer> categorySet = Sets.newHashSet();
        if (categoryId != MallConst.ROOT_PARENT_ID) {
            categorySet.add(categoryId);
        }
        //递归子节点
        findDeepChildrenCategory(categoryId, allCategoryList, categorySet);
        List<Integer> categoryList = categorySet.stream().collect(Collectors.toList());
        return ResponseVo.success(categoryList);
    }

    /**
     * 递归子节点
     *
     * @param categoryId
     * @param allCategoryList
     * @param categorySet     递归后寻找到的子节点列表
     */
    public void findDeepChildrenCategory(Integer categoryId,
                                         List<Category> allCategoryList,
                                         Set<Integer> categorySet) {
        List<Category> categories = allCategoryList.stream()
                .filter(category -> categoryId.equals(category.getParentId()))
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(categories)) {
            return;
        }
        for (Category category : categories) {
            categorySet.add(category.getId());
            findDeepChildrenCategory(category.getId(), allCategoryList, categorySet);
        }
    }

}
