package com.lxc.mall2.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by 82138 on 2019/5/3.
 */
@Configuration
public class WebConfigurer implements WebMvcConfigurer{
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(new AuthorityInterceptor())
                .addPathPatterns("/manage/**");

        registry.addInterceptor(new CrossOriginInterceptor())
                .addPathPatterns("/**");
    }

}
