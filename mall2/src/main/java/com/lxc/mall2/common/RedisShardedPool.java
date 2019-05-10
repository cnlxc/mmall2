package com.lxc.mall2.common;

import com.lxc.mall2.util.PropertiesUtil;
import redis.clients.jedis.*;
import redis.clients.util.Hashing;
import redis.clients.util.Sharded;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 82138 on 2019/5/3.
 */
public class RedisShardedPool {
    private static ShardedJedisPool pool;//jedis连接池
    private static Integer maxTotal = Integer.parseInt(PropertiesUtil.getProperty("redis.max.total","20")); //最大连接数
    private static Integer maxIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.max.idle","20"));//在jedispool中最大的idle状态(空闲的)的jedis实例的个数
    private static Integer minIdle = Integer.parseInt(PropertiesUtil.getProperty("redis.min.idle","20"));//在jedispool中最小的idle状态(空闲的)的jedis实例的个数

    private static Boolean testOnBorrow = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.borrow","true"));//在borrow一个jedis实例的时候，是否要进行验证操作，如果赋值true。则得到的jedis实例肯定是可以用的。
    private static Boolean testOnReturn = Boolean.parseBoolean(PropertiesUtil.getProperty("redis.test.return","true"));//在return一个jedis实例的时候，是否要进行验证操作，如果赋值true。则放回jedispool的jedis实例肯定是可以用的。

    private static String redisIp_1 = PropertiesUtil.getProperty("redis_1.ip");
    private static Integer redisPort_1 = Integer.parseInt(PropertiesUtil.getProperty("redis_1.port"));
    private static String redisIp_2 = PropertiesUtil.getProperty("redis_2.ip");
    private static Integer redisPort_2 = Integer.parseInt(PropertiesUtil.getProperty("redis_2.port"));

    private static void initPool(){
        //**********************CONFIGURATION****************
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(maxTotal);
        config.setMaxIdle(maxIdle);
        config.setMinIdle(minIdle);

        config.setTestOnBorrow(testOnBorrow);
        config.setTestOnReturn(testOnReturn);

        config.setBlockWhenExhausted(true);//连接耗尽的时候，是否阻塞，false会抛出异常，true阻塞直到超时。默认为true。
        //**********************CONFIGURATION****************
        //SHARD REDIS
        List<JedisShardInfo> jedisShardInfoList = new ArrayList<>(2);
        JedisShardInfo info1 = new JedisShardInfo(redisIp_1,redisPort_1,1000*2,"redis_1");
        JedisShardInfo info2 = new JedisShardInfo(redisIp_2,redisPort_2,1000*2,"redis_2");

        jedisShardInfoList.add(info1);
        jedisShardInfoList.add(info2);
        pool = new ShardedJedisPool(config,jedisShardInfoList, Hashing.MURMUR_HASH, Sharded.DEFAULT_KEY_TAG_PATTERN);
    }

    static{
        initPool();
    }

    public static ShardedJedis getJedis(){
        return pool.getResource();
    }


    public static void returnBrokenResource(ShardedJedis jedis){
        pool.returnBrokenResource(jedis);
    }



    public static void returnResource(ShardedJedis jedis){
        pool.returnResource(jedis);
    }

}
