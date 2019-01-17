package com.lock.lock;

import redis.clients.jedis.Jedis;

import com.redis.RedisPool;
import com.redis.RedisPoolUtil;
import com.redis.RedisShardedPoolUtil;

public class RedisTest {

	
	

    public static void main(String[] args) {
    	
        //Jedis jedis = RedisPool.getJedis();

         RedisPoolUtil.set("keyTest","value");

          String value = RedisPoolUtil.get("keyTest");
          System.out.println(value);
          RedisPoolUtil.setEx("keyex","valueex",60*10);
  
          RedisPoolUtil.expire("keyTest",60*20);

          RedisPoolUtil.del("keyTest");


       // String aaa = RedisPoolUtil.get(null);
       // System.out.println(aaa);

        System.out.println("end");


    }
}
