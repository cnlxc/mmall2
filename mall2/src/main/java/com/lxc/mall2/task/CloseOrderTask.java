package com.lxc.mall2.task;

import ch.qos.logback.classic.gaffer.PropertyUtil;
import com.lxc.mall2.common.Const;
import com.lxc.mall2.common.RedisShardedPool;
import com.lxc.mall2.service.IOrderService;
import com.lxc.mall2.util.PropertiesUtil;
import com.lxc.mall2.util.ShardedRedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Created by 82138 on 2019/5/11.
 */
@EnableScheduling
@Component
@Slf4j
public class CloseOrderTask {
    @Autowired
    IOrderService iOrderService;
    private int hour = Integer.parseInt(PropertiesUtil.getProperty("schedule.hour","2") );
    //@Scheduled(cron = "0 */1 * * * ?")//每一分钟执行一次
    public void closeOrder() {
        log.info("定时任务启动");

        iOrderService.closeOrder(hour);
        log.info("定时任务结束");
    }

    /**
     * redis锁使得只有一个服务器启动关单任务
     */
    @Scheduled(cron = "0 */1 * * * ?")//每一分钟执行一次
    public void closeOrderV2(){
        log.info("关单定时任务启动");
        //拿到配置的超时时间
        Long timeout = Long.parseLong(PropertiesUtil.getProperty("schedule.lock.duration","5000"));
        //设置锁
        Long getLockResult = ShardedRedisUtil.setnx(Const.RedisLock.REDIS_LOCK_KEY,String.valueOf(timeout+System.currentTimeMillis() ) );
        if(getLockResult != null && getLockResult == 1){
            //获得锁之后设置有效期防止死锁
            ShardedRedisUtil.expire(Const.RedisLock.REDIS_LOCK_KEY,5000 );
            iOrderService.closeOrder(hour);
            //释放锁
            ShardedRedisUtil.del(Const.RedisLock.REDIS_LOCK_KEY);
        }else{
            log.info(Thread.currentThread() + " ; 没有获得关单锁。");

        }
        log.info("关单定时任务关闭");
    }


}
