package com.lxc.mall2;

import com.lxc.mall2.service.IProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class Mall2ApplicationTests {

	@Autowired
	IProductService productService;

	@Test
	public void contextLoads() {
	}

	@Test
	public void transactionTest(){
		productService.transactionTest();
	}
}

