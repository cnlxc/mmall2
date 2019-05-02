package com.lxc.mall2;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.lxc.mall2.dao")
public class Mall2Application {

	public static void main(String[] args) {
		SpringApplication.run(Mall2Application.class, args);
	}

}
