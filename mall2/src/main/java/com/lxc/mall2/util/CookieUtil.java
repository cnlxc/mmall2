package com.lxc.mall2.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * Created by 82138 on 2019/5/2.
 */
@Slf4j
public class CookieUtil {
    private static final String COOKIE_NAME = "LOGIN_TOKEN";
    private static final String COOKIE_DOMAIN = "lxc.com";

    public static void WriteLoginToken(HttpServletResponse response,String token){
        Cookie cookie = new Cookie(COOKIE_NAME,token);
        cookie.setMaxAge(60*60*24);
//20190530 lv 跨域测试 先注销
        //cookie.setDomain(COOKIE_DOMAIN);
//20190530 lv 跨域测试 先注销
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        response.addCookie(cookie);
    }

    public static void delLoginCookie(HttpServletRequest request,HttpServletResponse response){
        Cookie[] cks = request.getCookies();
        Arrays.asList(cks).stream()
                .filter(cookie -> {
                    if(StringUtils.equals(cookie.getName(),COOKIE_NAME)) return true;
                    return false;
                })
                .forEach(cookie1 -> {
                    cookie1.setDomain(COOKIE_DOMAIN);
                    cookie1.setMaxAge(0);
                    cookie1.setPath("/");
                    log.info("debug :"+cookie1.getName());
                    response.addCookie(cookie1);
                });
    }
    public static String readLoginCookie(HttpServletRequest request){
        Cookie[] cks = request.getCookies();
        if(cks == null){
            log.info("cookie is null");
            return null;
        }
        for(Cookie cookie : cks){
            log.info("read cookieName: {},cookieValue:{}",cookie.getName(),cookie.getValue());
            if(StringUtils.equals(cookie.getName(),COOKIE_NAME)){
                log.info("cookie {} value is {}",COOKIE_NAME,cookie.getName() );
                return cookie.getValue();
            }
        }
        return  null;
    }

}
