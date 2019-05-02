package com.lxc.mall2.controller.backend;

import com.lxc.mall2.common.Const;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.pojo.User;
import com.lxc.mall2.service.IUserService;
import com.lxc.mall2.util.CookieUtil;
import com.lxc.mall2.util.JsonUtil;
import com.lxc.mall2.util.RedisPoolUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by 82138 on 2018/8/14.
 */

@Controller
@RequestMapping("/manage/user")
public class UserManageController {
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value="/login.do",method = RequestMethod.POST)
    public ServerResponse<User> login(String username, String password, HttpServletRequest request,HttpServletResponse httpResponse,HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            User user = response.getData();

            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                //说明登陆的是管理员
                String userObjStr = JsonUtil.obj2String(user);
                CookieUtil.WriteLoginToken(httpResponse,session.getId());
                RedisPoolUtil.set(session.getId(),userObjStr);

                //session.setAttribute(Const.CURRENT_USER, user);
                return response;
            }
        }
        return ServerResponse.createByErrorMessage("不是管理员,wufadenglu");
    }
}
