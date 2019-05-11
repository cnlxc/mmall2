package com.lxc.mall2.dao;

import com.lxc.mall2.pojo.Order;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    Order selectOrderByUserIdAndOrderNo(@Param("userId") Integer userId, @Param("orderId") Long orderId);

    Order selectByOrder(long orderNo);

    List<Order> selectOrderByUserId(Integer userId);

    List<Order> selectAllOrder();

    List<Order> selectOrderByStatusAndCreatetime(@Param("status") Integer status,@Param("date") String date);

    void closeOrderByNo(@Param("orderNo") long orderNo);
}