package com.lxc.mall2.controller.backend;

import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.pojo.User;
import com.lxc.mall2.service.IOrderService;
import com.lxc.mall2.service.IUserService;
import com.lxc.mall2.util.CookieUtil;
import com.lxc.mall2.util.JsonUtil;
import com.lxc.mall2.util.ShardedRedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by 82138 on 2018/10/6.
 */

@Controller
@RequestMapping("manage/order")
public class OrderManageController {
    @Autowired
    IUserService iUserService;
    @Autowired
    IOrderService iOrderService;

    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse orderList(HttpServletRequest request, @RequestParam(value="pageNum",defaultValue="1")  int pageNum, @RequestParam(value="pageSize",defaultValue = "10") int pageSize) {
        //User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(ShardedRedisUtil.get(loginToken),User.class);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请以管理员身份登陆");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageList(pageNum,pageSize);
        }else
            return ServerResponse.createByErrorMessage("不是管理员,无权限");
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public ServerResponse orderDetail(HttpServletRequest request, Long orderNo) {
        //User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(ShardedRedisUtil.get(loginToken),User.class);

        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请以管理员身份登陆");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageDetail(orderNo);
        }else
            return ServerResponse.createByErrorMessage("不是管理员,无权限");
    }

    @RequestMapping("search.do")
    @ResponseBody
    public ServerResponse orderSearch(HttpServletRequest request, Long orderNo, @RequestParam(value="pageNum",defaultValue="1")  int pageNum,
                                      @RequestParam(value="pageSize",defaultValue = "10") int pageSize) {
       // User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(ShardedRedisUtil.get(loginToken),User.class);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请以管理员身份登陆");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageSearch(orderNo,pageNum,pageSize);
        }else
            return ServerResponse.createByErrorMessage("不是管理员,无权限");
    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public ServerResponse orderSendGoods(HttpServletRequest request, Long orderNo) {
       // User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(ShardedRedisUtil.get(loginToken),User.class);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆，请以管理员身份登陆");
        }
        if(iUserService.checkAdminRole(user).isSuccess()) {
            return iOrderService.manageSendGoods(orderNo);
        }else
            return ServerResponse.createByErrorMessage("不是管理员,无权限");
    }

}
