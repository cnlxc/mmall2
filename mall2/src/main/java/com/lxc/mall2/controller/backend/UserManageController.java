package com.lxc.mall2.controller.backend;

import com.lxc.mall2.common.Const;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.pojo.User;
import com.lxc.mall2.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    public ServerResponse<User> login(String username, String password, HttpSession session) {
        ServerResponse<User> response = iUserService.login(username, password);
        if (response.isSuccess()) {
            User user = response.getData();

            if (user.getRole() == Const.Role.ROLE_ADMIN) {
                //说明登陆的是管理员
                session.setAttribute(Const.CURRENT_USER, user);
                return response;
            }
        }
        return ServerResponse.createByErrorMessage("不是管理员,wufadenglu");
    }
}
