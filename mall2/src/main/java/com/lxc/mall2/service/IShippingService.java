package com.lxc.mall2.service;

import com.github.pagehelper.PageInfo;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.pojo.Shipping;

/**
 * Created by 82138 on 2018/9/9.
 */
public interface IShippingService {

    ServerResponse add(Integer userId, Shipping shipping);

    ServerResponse del(Integer UserId, Integer shippingId);

    ServerResponse update(Integer UserId, Shipping shipping);

    ServerResponse<Shipping> select(Integer UserId, Integer shippingId);

    ServerResponse<PageInfo> list(Integer userId, Integer pageNum, Integer pageSize);

}
