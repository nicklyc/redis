package com.service;

import java.util.concurrent.TimeUnit;

import org.redisson.api.RLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.annotation.DistributedLock;
import com.redis.RedisPoolUtil;
import com.redis.redssion.RedissonManager;

@Service
public class DistributedLockTestService {
	private static Logger log = LoggerFactory.getLogger(DistributedLockTestService.class);
    @Autowired
    private  RedissonManager redissonManager;
    
	/**
	 * 模拟业务处理的读改写
	 */
	 public void handle(String userCount){
	    	//模拟业务数据   读改写
		     //TimeUnit.MILLISECONDS.sleep(timeout);
	    	 String count = RedisPoolUtil.get(userCount);
	    	 int countNum = Integer.parseInt(count);
	    	 countNum=countNum+2;
	    	 RedisPoolUtil.set(userCount, countNum+"");
	    	 log.info(count+":count");
	    }
	 
	 
		/**
		 * 加锁的业务读写
		 */
		 public void handle2(String userCount){
			 String lockey=userCount+"lockey";
			 RLock lock = redissonManager.getRedisson().getLock(lockey);
			// RLock lock = redissonManager.getRedisson().getFairLock(lockey);
		        boolean getLock = false;
		        try {
//		        	// 锁住3秒自动解锁，
//		        	lock.lock(3, TimeUnit.SECONDS);
//		        	if(lock.isHeldByCurrentThread()){
//		        		getLock=true;
//	            	    handle(userCount);
//		        	}else{
//		        		 log.info("Redisson没有获取到分布式锁:{},ThreadName:{}",lockey,Thread.currentThread().getName());
//		        	}
		            if(getLock = lock.tryLock(3,5, TimeUnit.SECONDS)){
		                log.info("获取到锁:{},ThreadName:{}",lockey,Thread.currentThread().getName());
		                handle(userCount);
    		            }else{
		                log.info("没有获取到锁:{},ThreadName:{}",lockey,Thread.currentThread().getName());
		            }
		        } catch (Exception e) {
		            log.error("锁获取异常",e);
		        } finally {
		            if(!getLock){
		                return;
		            }
		            lock.unlock();
		            log.info("释放锁");
		        }
		      
		       
		    }
		 
		 
		 
		 
		 
		 
			  /**
			   * 
			   * @param userCount
			   */
		     @DistributedLock(argNum = 1, lockNamePost = ".lock")
			 public void handle3(String userCount){
		    	    handle(userCount);
			    }
			 
			 
}
