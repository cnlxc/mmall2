package com.lxc.mall2;

import com.lxc.mall2.controller.common.SessionExpireResetFilter;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;


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
		filterRegistrationBean.addUrlPatterns("/*do");
		return filterRegistrationBean;
	}

/*	*//**
	 * springSession版本實現單點登陸的過濾器，該過濾器會包裝session對象，實現代碼無侵入
	 * @return
     *//*
	@Bean
	FilterRegistrationBean springSessionFilter(){
		FilterRegistrationBean filterRegistrationBean = new FilterRegistrationBean();
		filterRegistrationBean.setFilter(new DelegatingFilterProxy());
		filterRegistrationBean.setName("loginTokenTimeResetFilter");
		filterRegistrationBean.setOrder(2);
		filterRegistrationBean.addUrlPatterns("*//*.do");
		return filterRegistrationBean;
	}*/


}
