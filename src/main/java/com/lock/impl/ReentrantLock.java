package com.lock.impl;


import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import com.lock.model.LockInfo;

public class ReentrantLock extends AbstractLock {

    public ReentrantLock(RedissonClient redissonClient,LockInfo lockInfo) {
        this.redissonClient = redissonClient;
        this.lockInfo = lockInfo;
    }
    public ReentrantLock(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
       
    }

    @Override
    protected RLock getLock(String name) {
        return redissonClient.getLock(name);
    }
}
