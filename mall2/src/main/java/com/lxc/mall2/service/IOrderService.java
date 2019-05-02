package com.lxc.mall2.service;

import com.github.pagehelper.PageInfo;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.vo.OrderProductVo;
import com.lxc.mall2.vo.OrderVo;

import java.util.Map;

/**
 * Created by 82138 on 2018/9/15.
 */
public interface IOrderService {
    ServerResponse buy(Long orderNo, Integer userId, String path);

    ServerResponse aliCallBack(Map<String, String> params);

    ServerResponse queryOrderStatus(Integer userId, long orderNo);

    ServerResponse createOrder(Integer userId, Integer shippingId);

    ServerResponse<String> cancle(Integer userId, Long orderNo);

    ServerResponse<OrderProductVo> getOrderCartProduct(Integer userId);

    ServerResponse getOrderDetial(Integer userId, Long orderNo);

    ServerResponse<PageInfo> getOrderList(Integer userId, int pageNum, int PageSize);

    ServerResponse<PageInfo> manageList(int pageNum, int pageSize);

    ServerResponse<OrderVo> manageDetail(Long orderNo);

    ServerResponse<PageInfo> manageSearch(Long orderNo, int pageNum, int pageSize);

    ServerResponse<String> manageSendGoods(Long orderNo);
}
