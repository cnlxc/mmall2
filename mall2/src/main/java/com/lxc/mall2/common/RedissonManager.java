package com.lxc.mall2.common;

import com.lxc.mall2.util.PropertiesUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;

/**
 * Created by 82138 on 2019/5/11.
 */
@Component
@Slf4j
public class RedissonManager {

    private Config config = new Config();
    private Redisson redisson =null;

    private static String redisIp_1 = PropertiesUtil.getProperty("redis_1.ip");
    private static String redisPort_1 = PropertiesUtil.getProperty("redis_1.port");
    //private static String redisIp_2 = PropertiesUtil.getProperty("redis_2.ip");
    //private static String redisPort_2 = PropertiesUtil.getProperty("redis_2.port");

    //call method
    public Redisson getRedisson(){
        return redisson;
    }
    @PostConstruct
    private void init(){
        try{
            config.useSingleServer().setAddress("redis://127.0.0.1:6379");//redisIp_1 +":"+redisPort_1
            redisson = (Redisson) Redisson.create(config);
        }catch (Exception e){
            log.error("error in init Redisson",e);
        }

    }

}
