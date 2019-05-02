package com.lxc.mall2.common;


import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by 82138 on 2018/8/13.
 * 廢棄改爲Redis存儲
 */
public class TokenCache {
    private static Logger logger = LoggerFactory.getLogger(TokenCache.class);
    //本地缓存，存放键值对，使用build模式构建，初始容量1000，最大容量10000，有效期12小时
    private static LoadingCache<String,String> localCache = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(10000).expireAfterAccess(12, TimeUnit.HOURS).
            build(new CacheLoader<String, String>() {
                @Override
                //当找不到值的时候，会调用该方法，这里我们返回一个“null”字符串，下面的get方法就用到了这里的null
                public String load(String s) throws Exception {
                    return "null";
                }
            });

    public static void setKey(String key,String value) {
        localCache.put(key,value);
    }

    public static String getKey(String key) {
        String value = null;
        try {
            value = localCache.get(key);
            if(value == "null") {
                return null;
            }
        } catch (ExecutionException e) {
            logger.error("localcache get error",e);
        }
        return value;
    }

}
