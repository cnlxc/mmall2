package com.lxc.mall2.config;

import com.lxc.mall2.service.IProductService;
import com.lxc.mall2.service.Impl.ProductServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import java.util.Properties;

/**
 * Created by 82138 on 2019/5/25.
 * this class not be used .
 */
@Configuration
public class RmiConfigurer {


    @Bean
    IProductService productService(){
        return new ProductServiceImpl();
    }

    @Bean(name = "hessianExportedProductService")
    public HessianServiceExporter hessianExportedProductService(IProductService productService){
        HessianServiceExporter hessianServiceExporter = new HessianServiceExporter();
        hessianServiceExporter.setService(productService);
        hessianServiceExporter.setServiceInterface(IProductService.class);
        return hessianServiceExporter;
    }

    @Bean
    HandlerMapping hessianMapping(){
        SimpleUrlHandlerMapping hessianMapping = new SimpleUrlHandlerMapping();
        Properties mappings = new Properties();
        mappings.setProperty("/product.service","hessianExportedProductService");
        hessianMapping.setMappings(mappings);
        return hessianMapping;
    }
}
