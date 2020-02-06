package com.lxc.mall2.task;

import ch.qos.logback.classic.gaffer.PropertyUtil;
import com.lxc.mall2.common.Const;
import com.lxc.mall2.common.RedisShardedPool;
import com.lxc.mall2.common.RedissonManager;
import com.lxc.mall2.service.IOrderService;
import com.lxc.mall2.util.PropertiesUtil;
import com.lxc.mall2.util.ShardedRedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Created by 82138 on 2019/5/11.
 */
@EnableScheduling
@Component
@Slf4j
public class CloseOrderTask {
    @Autowired
    IOrderService iOrderService;

    @Autowired
    RedissonManager redissonManager;

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
    //@Scheduled(cron = "0 */1 * * * ?")//每一分钟执行一次
    public void closeOrderV2(){
        log.info("关单定时任务尝试启动");
        //拿到配置的超时时间
        Long timeout = Long.parseLong(PropertiesUtil.getProperty("schedule.lock.duration","5000"));
        //如果不存在则设置锁
        Long getLockResult = ShardedRedisUtil.setnx(Const.RedisLock.REDIS_LOCK_KEY,String.valueOf(timeout+System.currentTimeMillis() ) );
        if(getLockResult != null && getLockResult == 1){
            closeOrderTask();
        }else{
            log.info(Thread.currentThread() + " ; 没有获得关单锁。");
            String oldExpireTime1 = ShardedRedisUtil.get(Const.RedisLock.REDIS_LOCK_KEY);
            if(oldExpireTime1 != null && System.currentTimeMillis() > Long.valueOf(oldExpireTime1) ){
                //getset是原子操作，如果两次时间相等则表明没问题，如果两次时间不等，我们不进行任务处理，虽然设置了一个新的时间，且没有调用expire方法
                //但不等意味着别的进程获得了锁，别的进程进行任务处理，最终会释放这个锁，所以即使时间不等的时候我们这个时间设置并不影响。
                String oldExpireTime2 = ShardedRedisUtil.getSet(Const.RedisLock.REDIS_LOCK_KEY,String.valueOf(System.currentTimeMillis() + timeout ) );
                if(oldExpireTime2 ==null ||(oldExpireTime2 !=null &&oldExpireTime1 == oldExpireTime2))
                    closeOrderTask();
                else
                    //虽然getset了,
                    log.info("关单锁被其他进程获得");
            }else //现在还没到过期时间，我们不满足获得条件 不获得锁
            //若过期时间为null，虽然这时可以获得锁，但我们不再调用getset了，避免逻辑复杂，失败下次再setnx来判断就好了
                log.info("关单锁被其他进程获得");//
        }

    }
    //REDISSON版直接使用
    //@Scheduled(cron = "0 */1 * * * ?")//每一分钟执行一次
    public void closeOrderV3(){
        RLock lock = redissonManager.getRedisson().getLock(Const.RedisLock.REDIS_LOCK_KEY);
        boolean getLock = false;
        try{
            if(getLock =lock.tryLock(0,5, TimeUnit.SECONDS))
                iOrderService.closeOrder(hour);
            else log.info("Redisson分布式锁获取失败");
        }catch (InterruptedException e){
            log.error("{} 获取锁中断",Const.RedisLock.REDIS_LOCK_KEY);
        }finally {
            if(getLock == false)
                return;
            lock.unlock();
            log.info("{} 锁释放",Const.RedisLock.REDIS_LOCK_KEY);
        }

    }

    /**
     * 获得锁后被调用，先设置有效期，即这个锁5秒后要被释放
     * 当然，任务成功的快的话可直接释放锁
     */
    private void closeOrderTask(){
        log.info("关单定时任务开始");
        //获得锁之后设置有效期防止死锁
        ShardedRedisUtil.expire(Const.RedisLock.REDIS_LOCK_KEY,5000 );
        iOrderService.closeOrder(hour);
        //释放锁
        ShardedRedisUtil.del(Const.RedisLock.REDIS_LOCK_KEY);
        log.info("关单定时任务执行并关闭");
    }
}
