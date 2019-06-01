package com.lxc.mall2.dao;

import com.lxc.mall2.pojo.Cart;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CartMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Cart record);

    int insertSelective(Cart record);

    Cart selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Cart record);

    int updateByPrimaryKey(Cart record);

    Cart selectCartByUserIdProductId(@Param("userId") Integer userId, @Param("productId") Integer productId);

    List<Cart> selectByUserId(Integer userId);

    Integer selectCheckedByUserId(Integer userId);

    void deleteByUserIdProductId(@Param("userId") Integer userId, @Param("productIdList") List<String> productIdList);

    void selectOrUnselectAllProduct(@Param("userId") Integer userId, @Param("productId") Integer productId, @Param("checked") Integer checked);

    List<Cart> selectCheckedCartByUserId(Integer userId);

    Integer selectCountByUserId(Integer userId);
}