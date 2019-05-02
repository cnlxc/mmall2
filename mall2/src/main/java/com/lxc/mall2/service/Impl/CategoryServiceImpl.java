package com.lxc.mall2.service.Impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.dao.CategoryMapper;
import com.lxc.mall2.pojo.Category;
import com.lxc.mall2.service.ICategoryService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Created by 82138 on 2018/8/15.
 */

@Service("iCategoryService")
public class CategoryServiceImpl implements ICategoryService{

    @Autowired
    CategoryMapper categoryMapper;



    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);
    public ServerResponse addCategory(String categoryName,Integer parentId) {
        if(parentId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("添加品类参数错误");
        }

        Category category = new Category();
        category.setParentId(parentId);
        category.setName(categoryName);
        category.setStatus(true);
        int resultCount = categoryMapper.insert(category);
        if(resultCount > 0) {
            return ServerResponse.createBySuccess("添加品类成功");
        }
        return ServerResponse.createByErrorMessage("添加品类失败");
    }

    public ServerResponse updateCategoryName(Integer categoryId,String categoryName) {
        if(categoryId == null || StringUtils.isBlank(categoryName)) {
            return ServerResponse.createByErrorMessage("更新品类参数错误");
        }
        //视频没有这个判断，个人认为还是需要判断一下品类ID是否有
       // if(categoryMapper.selectByPrimaryKey(categoryId) != null){return ServerResponse.createByErrorMessage("该品类不存在，无法更新名字")}
        Category category = new Category();
        category.setId(categoryId);
        category.setName(categoryName);

        int resultCount = categoryMapper.updateByPrimaryKeySelective(category);
        if(resultCount > 0) { return ServerResponse.createBySuccess("品类名字更新成功");}
        return ServerResponse.createByErrorMessage("品类名字更新失败");
    }

    public ServerResponse<List<Category>> getChildrenParallelCategoryId(int categotyId) {
        List<Category> categoryList = categoryMapper.selectChildrenCategoryIdByParentId(categotyId);
        if(CollectionUtils.isEmpty(categoryList)) {
            logger.info("未找到当前分类的子分类");
        }
        return ServerResponse.createBySuccess(categoryList);
    }

    public ServerResponse<List<Integer>> getDeepCategoryId(int categoryId) {
        Set<Category> set = Sets.newHashSet();

        findChildrenCategory(set,categoryId);
        List<Integer> list = Lists.newArrayList();
        for(Category category : set) {
            list.add(category.getId());
        }
        return ServerResponse.createBySuccess(list);

    }
    /*
    递归查询子节点，第一步把该节点放入Set里，第二步遍历找到的子节点，遍历操作为调用本身
    参数一为最终容纳所有节点的容器，参数二为初始节点
     */
    private Set<Category> findChildrenCategory(Set<Category> categorySet,int categoryId) {
        Category category = categoryMapper.selectByPrimaryKey(categoryId);
        if(category != null) {
            categorySet.add(category);
        }
        List<Category> list = categoryMapper.selectChildrenCategoryIdByParentId(categoryId);
        for(Category categoryItem : list) {
            findChildrenCategory(categorySet,categoryItem.getId());
        }
        return categorySet;

    }


}
