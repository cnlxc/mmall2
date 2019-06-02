package com.lxc.mall2.controller.portal;

import com.lxc.mall2.common.Const;
import com.lxc.mall2.common.ResponseCode;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.pojo.User;
import com.lxc.mall2.service.IUserService;
import com.lxc.mall2.util.CookieUtil;
import com.lxc.mall2.util.JsonUtil;
import com.lxc.mall2.util.ShardedRedisUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Created by 82138 on 2018/8/11.
 */
@Controller
@RequestMapping("/user/")
public class UserController {
    /**
     * 用户登录
     * @param username
     * @param passsword
     * @param session
     * @return
     */
    @Autowired
    private IUserService iUserService;

    @RequestMapping(value="login.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpResponse, HttpServletRequest request) {
        ServerResponse response = iUserService.login(username,password);
        if(response.isSuccess()) {
            //session.setAttribute(Const.CURRENT_USER,response.getData());

            User user = (User)response.getData();
            CookieUtil.WriteLoginToken(httpResponse,session.getId() ); //将loginToken加入cookie
            ShardedRedisUtil.setEx(session.getId(), JsonUtil.obj2String(user),Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        //httpResponse.addHeader("Access-Control-Allow-Origin", "*");
        return response;
    }

    @RequestMapping(value="logout.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> logout(HttpServletRequest request,HttpServletResponse response){
        CookieUtil.delLoginCookie(request,response);
        //session.removeAttribute(Const.CURRENT_USER);
        return ServerResponse.createBySuccess();
    }

    @RequestMapping(value="register.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping(value="check_valid.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> checkValid(String str ,String type) {
       return iUserService.checkValid(str,type);
    }

    @RequestMapping(value="get_user_info.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getUserInfo(HttpServletRequest request) {
        //User user = (User) session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(ShardedRedisUtil.get(loginToken),User.class);
        if(user == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),ResponseCode.NEED_LOGIN.getDesc());
        }
        return ServerResponse.createBySuccess(user);
    }

    @RequestMapping(value="forget_get_question.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetGetQusetion(String username) {
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value="forget_check_answer.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetCheckAnswer(String username,String question,String answer) {
        return iUserService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value="forget_password_reset.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> forgetPasswordReset(String username,String newPassword,String forgetToken) {
          return iUserService.forgetPasswordReset(username,newPassword,forgetToken);
    }

    @RequestMapping(value="password_reset.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<String> passwordReset(HttpServletRequest request, String oldPassword, String newPassword) {
        //User user = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User user = JsonUtil.string2Obj(ShardedRedisUtil.get(loginToken),User.class);
        if(user == null) {
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        return iUserService.passwordReset(oldPassword,newPassword,user);
    }

    @RequestMapping(value="update_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> updateInformation(HttpSession session,HttpServletRequest request, User user) {
        //User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User currentUser = JsonUtil.string2Obj(ShardedRedisUtil.get(loginToken),User.class);
        if(currentUser == null) {
            return ServerResponse.createByErrorMessage("用户未登陆");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        ServerResponse<User> response =  iUserService.updateInformation(user);
        if(response.isSuccess()) {
            response.getData().setUsername(currentUser.getUsername());
            ShardedRedisUtil.set(session.getId(),JsonUtil.obj2String(response.getData()) );
            //session.setAttribute(Const.CURRENT_USER,response.getData());
        }

        return response;
    }
    @RequestMapping(value="get_information.do",method = RequestMethod.POST)
    @ResponseBody
    public ServerResponse<User> getInformation(HttpServletRequest request) {
       // User currentUser = (User)session.getAttribute(Const.CURRENT_USER);
        String loginToken = CookieUtil.readLoginCookie(request);
        //判断Redis中是否有该Session
        if(StringUtils.isEmpty(loginToken)){
            return ServerResponse.createByErrorMessage("用户未登录，无法获取用户信息");
        }
        User currentUser = JsonUtil.string2Obj(ShardedRedisUtil.get(loginToken),User.class);
        if(currentUser == null) {
            return ServerResponse.createByErrorCodeMessage(ResponseCode.NEED_LOGIN.getCode(),"用户未登陆,需要强制登陆");
        }
        return iUserService.getInformation(currentUser.getId());

    }
}
