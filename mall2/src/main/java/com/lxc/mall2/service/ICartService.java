package com.lxc.mall2.service;

import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.vo.CartVo;

/**
 * Created by 82138 on 2018/9/2.
 */
public interface ICartService {

    ServerResponse<CartVo> add(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> update(Integer userId, Integer productId, Integer count);

    ServerResponse<CartVo> productDelete(Integer userId, String productIds);

    ServerResponse<CartVo> list(Integer userId);

    ServerResponse<CartVo> selectOrUnSelect(Integer userId, Integer productId, Integer checked);

    ServerResponse<Integer> getCartCount(Integer userId);
}
