package com.lxc.mall2.controller.common;

import com.lxc.mall2.common.Const;
import com.lxc.mall2.pojo.User;
import com.lxc.mall2.util.CookieUtil;
import com.lxc.mall2.util.JsonUtil;
import com.lxc.mall2.util.ShardedRedisUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by 82138 on 2019/5/2.
 */

public class SessionExpireResetFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        String loginToken = CookieUtil.readLoginCookie(httpServletRequest);
        //loginToken不为空证明这是登陆过的用户发来的请求
        if(StringUtils.isNotEmpty(loginToken)){
            //再把redis里的json字符串转为User对象，个人觉得没有必要，logintoken不为空就能证明用户存在了
            String userJsonStr = ShardedRedisUtil.get(loginToken);
            User user = JsonUtil.string2Obj(userJsonStr,User.class);
            if(user != null)
                ShardedRedisUtil.expire(loginToken, Const.RedisCacheExtime.REDIS_SESSION_EXTIME);
        }
        chain.doFilter(request,response);
    }

    @Override
    public void destroy() {

    }
}
