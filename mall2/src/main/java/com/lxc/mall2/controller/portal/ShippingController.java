package com.lxc.mall2.controller.portal;

import com.github.pagehelper.PageInfo;
import com.lxc.mall2.common.Const;
import com.lxc.mall2.common.ResponseCode;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.pojo.Shipping;
import com.lxc.mall2.pojo.User;
import com.lxc.mall2.service.IShippingService;
import com.lxc.mall2.util.CookieUtil;
import com.lxc.mall2.util.JsonUtil;
import com.lxc.mall2.util.RedisPoolUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * Created by 82138 on 2018/9/9.
 */

@Controller
@RequestMapping("/shipping/")
public class ShippingController {

    @Autowired
    IShippingService iShippingService;

    @RequestMapping("add.do")
    @ResponseBody
    public ServerResponse add(HttpServletRequest request, Shipping shipping){
        //User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(RedisPoolUtil.get(loginToken),User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.add(user.getId(),shipping);

    }

    @RequestMapping("del.do")
    @ResponseBody
    public ServerResponse del(HttpServletRequest request, Integer shippingId){
        //User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(RedisPoolUtil.get(loginToken),User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.del(user.getId(),shippingId);

    }

    @RequestMapping("update.do")
    @ResponseBody
    public ServerResponse update(HttpServletRequest request, Shipping shipping) {
        //User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(RedisPoolUtil.get(loginToken),User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.update(user.getId(),shipping);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public ServerResponse<Shipping> select(HttpServletRequest request, Integer shippingId) {
       // User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(RedisPoolUtil.get(loginToken),User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.select(user.getId(),shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value="pageNum",defaultValue = "1")Integer pageNum,
                                         @RequestParam(value="pageSize",defaultValue = "10")Integer pageSize,
                                         HttpServletRequest request) {
        //User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(RedisPoolUtil.get(loginToken),User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.list(user.getId(),pageNum,pageSize);
    }
}
