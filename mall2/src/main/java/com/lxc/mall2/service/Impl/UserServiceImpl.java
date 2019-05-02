package com.lxc.mall2.service.Impl;

import com.lxc.mall2.common.Const;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.common.TokenCache;
import com.lxc.mall2.dao.UserMapper;
import com.lxc.mall2.pojo.User;
import com.lxc.mall2.service.IUserService;
import com.lxc.mall2.util.MD5Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Created by 82138 on 2018/8/11.
 */
@Service("iUserService")
public class UserServiceImpl implements IUserService{

    @Autowired
    private UserMapper usermapper;

    @Override
    public ServerResponse login(String username, String password) {
        int resultCount = usermapper.checkUsername(username);
        if(resultCount == 0) {
            return ServerResponse.createByErrorMessage("用户名不存在");
        }

        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user = usermapper.selectlogin(username,md5Password);

        if(user == null) {
            return ServerResponse.createByErrorMessage("密码错误");
        }

        user.setPassword(org.apache.commons.lang3.StringUtils.EMPTY);

        return ServerResponse.createBySuccess("登陆成功",user);
    }

    @Override
    public ServerResponse register(User user) {
        //验证用户名
        ServerResponse validResponse = checkValid(user.getUsername(),Const.USERNAME);
        if(!validResponse.isSuccess()) {return validResponse;}
        //验证邮箱
        validResponse = checkValid(user.getEmail(),Const.EMAIL);
        if(!validResponse.isSuccess()) {return validResponse;}
        user.setRole(Const.Role.ROLE_CUSTOMER);
        //ND5加密
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = usermapper.insert(user);
        if(resultCount == 0) {
            return ServerResponse.createByErrorMessage("注册失败");
        }
        return ServerResponse.createBySuccess("注册成功");
    }
   //不存在返回TRUE
   public ServerResponse<String> checkValid(String str,String type) {
      if(org.apache.commons.lang3.StringUtils.isNotBlank(type)) {
         if(Const.USERNAME.equals(type)) {
             int resultCount = usermapper.checkUsername(str);
             if(resultCount > 0) {
                 return ServerResponse.createByErrorMessage("用户名已存在");
             }
         }

          if(Const.EMAIL.equals(type)) {
              int resultCount = usermapper.checkEmail(str);
              if(resultCount > 0) {
                  return ServerResponse.createByErrorMessage("邮箱已存在");
              }
          }
      }else return ServerResponse.createByErrorMessage("参数错误");
       return ServerResponse.createBySuccess("校验成功");
   }

    public ServerResponse selectQuestion(String username) {
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()) {

            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String question = usermapper.selectQuestionByUsername(username);
        if(org.apache.commons.lang3.StringUtils.isNoneBlank(question)) {
            return ServerResponse.createBySuccess(question);
        }
        return ServerResponse.createByErrorMessage("找回密码的问题是空的");
    }

    public ServerResponse<String> checkAnswer(String username,String question,String answer) {
        int resultCount = usermapper.checkAnswer(username,question,answer);
        if(resultCount > 0) {
            String uuid = UUID.randomUUID().toString();
            TokenCache.setKey("token_"+username,uuid);
            return ServerResponse.createBySuccess(uuid);
        }

        return ServerResponse.createByErrorMessage("问题答案错误");
    }

    public ServerResponse<String> forgetPasswordReset(String username,String newPassword,String forgetToken){
        if(org.apache.commons.lang3.StringUtils.isBlank(forgetToken)) {
            return ServerResponse.createByErrorMessage("参数错误，Token需要传递");
        }
        ServerResponse validResponse = this.checkValid(username,Const.USERNAME);
        if(validResponse.isSuccess()) {

            return ServerResponse.createByErrorMessage("用户不存在");
        }
        String token = TokenCache.getKey("token_"+username);
        if(org.apache.commons.lang3.StringUtils.isBlank(token)) {
            ServerResponse.createByErrorMessage("token无效，或者过期");
        }
        if(org.apache.commons.lang3.StringUtils.equals(token,forgetToken)) {
            String MD5Password = MD5Util.MD5EncodeUtf8(newPassword);
            int resultCount = usermapper.updatePasswordByUsername(username,MD5Password);
            if(resultCount > 0) return ServerResponse.createBySuccess("修改密码成功");
        }else
            return ServerResponse.createByErrorMessage("token无效");

        return ServerResponse.createByErrorMessage("修改密码失败");
    }

    public ServerResponse<String> passwordReset(String oldPassword,String newPassword,User user) {
        int resultCount = usermapper.checkPassword(MD5Util.MD5EncodeUtf8(oldPassword),user.getId());
        if(resultCount > 0) {
            user.setPassword(MD5Util.MD5EncodeUtf8(newPassword));
            int updateCount = usermapper.updateByPrimaryKeySelective(user);
            if(updateCount > 0)
                return ServerResponse.createBySuccess("密码更新成功");
            else
                return ServerResponse.createByErrorMessage("密码更新失败");
        }
        return ServerResponse.createByErrorMessage("旧密码错误");

    }

    public ServerResponse<User> updateInformation(User user) {
        int resultCount = usermapper.checkEmailByUserId(user.getId(),user.getEmail());
        if(resultCount > 0){return ServerResponse.createByErrorMessage("邮箱已被使用，请更换邮箱地址");}
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setAnswer(user.getAnswer());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setPhone(user.getPhone());

        int updateCount = usermapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount > 0)
            return ServerResponse.createBySuccess("信息更新成功",updateUser);
        return ServerResponse.createByErrorMessage("更新个人信息失败");
    }

    public ServerResponse<User> getInformation(int userId) {
        User user = usermapper.selectByPrimaryKey(userId);
        if(user == null) return ServerResponse.createByErrorMessage("找不到当前用户");
        user.setPassword(StringUtils.EMPTY);
        return ServerResponse.createBySuccess(user);

    }
    //检查是否是管理员
    public ServerResponse checkAdminRole(User user) {
       if(user != null && user.getRole().intValue() == Const.Role.ROLE_ADMIN) {
           return ServerResponse.createBySuccess();
       }
       return ServerResponse.createByErrorMessage("不是管理员");
    }
}
