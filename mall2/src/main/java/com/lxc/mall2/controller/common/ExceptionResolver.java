package com.lxc.mall2.controller.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 82138 on 2019/5/3.
 * 返回前臺的異常不要有堆棧信息，用JSON VIEW進行包裝
 */
@Slf4j
@Component
public class ExceptionResolver implements HandlerExceptionResolver{
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, @Nullable Object handler, Exception ex) {
        log.error("request {} exception",request.getRequestURI(),ex);
        ModelAndView modelAndView = new ModelAndView(new MappingJackson2JsonView() );
        modelAndView.addObject("status",response.getStatus() );
        modelAndView.addObject("msg","后台服務接口異常，請查看日志");
        modelAndView.addObject("data", ex.getMessage() );
        return modelAndView;
    }
}
