package com.lxc.mall2.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 82138 on 2019/5/28.
 * add crossOrigin permit for all requests
 */
@Slf4j
public class CrossOriginInterceptor implements HandlerInterceptor {
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        log.info("add corss permit for {}",request.getRequestURI());
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:8088");
        response.addHeader("Access-Control-Allow-Credentials","true");
        return true;
    }

}
