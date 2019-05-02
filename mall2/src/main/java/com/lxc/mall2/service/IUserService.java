package com.lxc.mall2.service;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.pojo.User;

/**
 * Created by 82138 on 2018/8/11.
 */
public interface IUserService {
    public ServerResponse login(String username, String password);

    public ServerResponse register(User user);

    public ServerResponse<String> checkValid(String str, String type);

    public ServerResponse selectQuestion(String username);

    public ServerResponse<String> checkAnswer(String username, String question, String answer);

    public ServerResponse<String> forgetPasswordReset(String username, String newPassword, String forgetToken);

    public ServerResponse<String> passwordReset(String oldPassword, String newPassword, User user);

    public ServerResponse<User> updateInformation(User user);

    public ServerResponse<User> getInformation(int userId);

    public ServerResponse checkAdminRole(User user);
}
