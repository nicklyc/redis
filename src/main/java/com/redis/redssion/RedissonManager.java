package com.redis.redssion;

import javax.annotation.PostConstruct;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.redis.PropertiesUtil;


@Component
public class RedissonManager {
	private static Logger logger = LoggerFactory.getLogger(RedissonManager.class);
    private Config config = new Config();

    private Redisson redisson = null;

    public Redisson getRedisson() {
        return redisson;
    }

    private static String redis1Ip = PropertiesUtil.getProperty("redis1.ip");
    private static Integer redis1Port = Integer.parseInt(PropertiesUtil.getProperty("redis1.port"));

    
    //Redssion  不支付分布式一致性算法，所以只能用一个redis
    @PostConstruct
    private void init(){
        try {
        	String address = new StringBuilder().append(redis1Ip).append(":").append(redis1Port).toString();
            config.useSingleServer().setAddress(address);//host:port

            redisson = (Redisson) Redisson.create(config);

            logger.info("初始化Redisson结束");
        } catch (Exception e) {
        	logger.error("redisson init error",e);
        }
    }



}
