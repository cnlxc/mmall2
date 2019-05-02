package com.lxc.mall2.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.dao.ShippingMapper;
import com.lxc.mall2.pojo.Shipping;
import com.lxc.mall2.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 82138 on 2018/9/9.
 */

@Service("iShippingService")
public class ShippingServiceImpl implements IShippingService {
    @Autowired
    ShippingMapper shippingMapper;

    public ServerResponse add(Integer userId, Shipping shipping) {
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount > 0) {
            Map result = Maps.newHashMap();
            result.put("shipping",shipping.getId());
            return ServerResponse.createBySuccess("新建地址成功",result);
        }
        return ServerResponse.createByErrorMessage("地址创建失败");
    }

    public ServerResponse del(Integer UserId,Integer shippingId){
        int resultCount = shippingMapper.deleteByUserIdShippingId(UserId,shippingId);
        if(resultCount > 0)
            return ServerResponse.createBySuccess("删除地址成功");
        return ServerResponse.createByErrorMessage("删除地址失败");
    }

    public ServerResponse update(Integer UserId,Shipping shipping) {
        shipping.setUserId(UserId);
        int resultCount = shippingMapper.updateByUserIdShippingId(shipping);
        if(resultCount >0) {
            return ServerResponse.createBySuccess("更新地址成功");
        }
        return ServerResponse.createByErrorMessage("更新地址失败");
    }

    public ServerResponse<Shipping> select(Integer UserId,Integer shippingId) {

        Shipping shipping = shippingMapper.selectByShippingIdUserId(UserId,shippingId);
        if(shipping != null) {
            return ServerResponse.createBySuccess("获取地址信息成功",shipping);
        }
        return ServerResponse.createByErrorMessage("获取地址失败");
    }
    public ServerResponse<PageInfo> list(Integer userId,Integer pageNum,Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return ServerResponse.createBySuccess(pageInfo);
    }
}
