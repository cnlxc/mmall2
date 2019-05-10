package com.lxc.mall2.config;

import com.google.common.collect.Maps;
import com.lxc.mall2.common.Const;
import com.lxc.mall2.common.ServerResponse;
import com.lxc.mall2.pojo.User;
import com.lxc.mall2.util.CookieUtil;
import com.lxc.mall2.util.JsonUtil;
import com.lxc.mall2.util.ShardedRedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by 82138 on 2019/5/3.
 */
@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor{
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        log.info("AuthorityInterceptor uri :"+request.getRequestURI() );

            HandlerMethod handlerMethod = (HandlerMethod)handler;
            String methodName = handlerMethod.getMethod().getName();
            String className = handlerMethod.getBean().getClass().getSimpleName();
            StringBuffer param = new StringBuffer();

            Map<String,String[]> paramMap = request.getParameterMap();
            Iterator<Map.Entry<String, String[]>> iterator= paramMap.entrySet().iterator();
            while(iterator.hasNext()){
                Map.Entry<String, String[]> entry = iterator.next();
                String value = Arrays.toString(entry.getValue() );
                param.append("param:"+ entry.getKey() +"value:"+ value+" ");
            }
            if(StringUtils.equals(methodName,"login")){//StringUtils.equals(className,"UserManageController") &&
                log.info("拦截器拦截到請求,className:{},methodName:{}",className,methodName);
                //登陸請求參數不打印
                return true;
            }
            log.info("拦截器拦截到請求,className：{}，methodName：{} ，requestParam{} ",className,methodName,param.toString() );

            User user =null;

            String loginToken = CookieUtil.readLoginCookie(request);

            if(StringUtils.isNotEmpty(loginToken)){
                String jsonUser = ShardedRedisUtil.get(loginToken);
                user = JsonUtil.string2Obj(jsonUser,User.class);
            }
            if(user == null || user.getRole().intValue() != Const.Role.ROLE_ADMIN){
                //不是管理員 攔截該請求
                response.reset();
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                if(user == null){
                    if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richTextImgUpload")){
                        Map map = Maps.newHashMap();
                        map.put("success","false");
                        map.put("msg","請登錄管理員");
                        String jsonResponse = JsonUtil.obj2String(map);
                        out.print(jsonResponse);
                    }else {
                        out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("用戶未登錄")));
                    }

                }else {
                    if(StringUtils.equals(className,"ProductManageController") && StringUtils.equals(methodName,"richtextImgUpload")){
                        Map resultMap = Maps.newHashMap();
                        resultMap.put("success",false);
                        resultMap.put("msg","无权限操作");
                        out.print(JsonUtil.obj2String(resultMap));
                    }else{
                        out.print(JsonUtil.obj2String(ServerResponse.createByErrorMessage("拦截器拦截,用户无权限操作")));
                    }
                }
                out.flush();
                out.close();
                return false;
            }

        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                            @Nullable ModelAndView modelAndView) throws Exception {
        log.info("postHandler");
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
                                 @Nullable Exception ex) throws Exception {
        log.info("afterCompletion");
    }

}
