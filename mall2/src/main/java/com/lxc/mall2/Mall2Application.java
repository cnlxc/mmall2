package com.lxc.mall2;

import com.lxc.mall2.controller.common.SessionExpireResetFilter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@MapperScan("com.lxc.mall2.dao")
public class Mall2Application {

	public static void main(String[] args) {
		SpringApplication.run(Mall2Application.class, args);
	}

	@Bean
	FilterRegistrationBean loginTokenTimeResetFilter(){
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new SessionExpireResetFilter());
		filterRegistrationBean.setName("loginTokenTimeResetFilter");
		filterRegistrationBean.setOrder(1);
		filterRegistrationBean.addUrlPatterns("/*");
		return filterRegistrationBean;
	}
}
