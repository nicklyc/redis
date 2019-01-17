package com.task;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PreDestroy;

import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.redis.RedisPoolUtil;
import com.redis.RedisShardedPoolUtil;
import com.redis.redssion.RedissonManager;


@Component
public class DistributedLockTest {
    private static ExecutorService  executor = Executors.newFixedThreadPool(10);
	private static Logger log = LoggerFactory.getLogger(DistributedLockTest.class);
   // @Autowired
    private static RedissonManager redissonManager=new RedissonManager();
    private String Lockkey="_LOCK";
    @PreDestroy
    public void delLock(){
   
        RedisShardedPoolUtil.del(Lockkey);

    }
    
    public void dealWith(String Lockkey){
        RLock lock = redissonManager.getRedisson().getLock(Lockkey);
        boolean getLock = false;
        try {
        	// 尝试获取锁，最多等待2秒，上锁以后5秒自动解锁，
            if(getLock = lock.tryLock(2,5, TimeUnit.SECONDS)){
                log.info("Redisson获取到分布式锁:{},ThreadName:{}",Lockkey,Thread.currentThread().getName());
                log.info("处理业务开始");
                //模拟处理业务数据耗时1秒
                SingleThreadAdd();
                log.info("处理业务结束");
            }else{
                log.info("Redisson没有获取到分布式锁:{},ThreadName:{}",Lockkey,Thread.currentThread().getName());
            }
        } catch (InterruptedException e) {
            log.error("Redisson分布式锁获取异常",e);
        } finally {
            if(!getLock){
                return;
            }
            lock.unlock();
            log.info("Redisson分布式锁释放锁");
        }
    }



    public static void main(String[] args) throws InterruptedException {
    	final DistributedLockTest test = new DistributedLockTest();
    	long timeMillis = System.currentTimeMillis();
    	for (int i = 0; i < 1000; i++) {
    		//test.SingleThreadAdd(
    		executor.submit(new Runnable() {
    			public void run() {
    				//test.SingleThreadAdd();
    				test.dealWith("lock");
    			}
    		});
    		
		}
    	System.out.println(RedisPoolUtil.get("count")+"==="+(System.currentTimeMillis()-timeMillis));
	}
    
    
    public void SingleThreadAdd(){
    	//模拟业务数据   读改写
    	 String count = RedisPoolUtil.get("count");
    	 int countNum = Integer.parseInt(count);
    	 countNum=countNum+2;
    	 RedisPoolUtil.set("count", countNum+"");
    }
    
    
    
    
    
    
    
    
    
    
    
    
    
    
/*
    private void delWinth(String lockName){
        RedisShardedPoolUtil.expire(lockName,3);//有效期3秒，防止死锁
        log.info("获取{},ThreadName:{}",lockName,Thread.currentThread().getName());
        try {
        	//模拟处理业务数据耗时
			TimeUnit.SECONDS.sleep(1);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
        RedisShardedPoolUtil.del(lockName);
        log.info("释放{},ThreadName:{}",lockName,Thread.currentThread().getName());
        log.info("===============================");
    }*/




}
