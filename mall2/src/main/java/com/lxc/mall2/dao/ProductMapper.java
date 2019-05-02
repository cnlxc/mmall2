package com.lxc.mall2.dao;

import com.lxc.mall2.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface ProductMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Product record);

    int insertSelective(Product record);

    Product selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Product record);

    int updateByPrimaryKey(Product record);

    List<Product> selectList();

    List<Product> selectByNameAndId(@Param("productId") Integer productId, @Param("productName") String productName);

    List<Product> selectByNameAndCatrgoryIds(@Param("prodcutName") String prodcutName, @Param("categoryIdList") List<Integer> categoryIdList);
}