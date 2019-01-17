package com.lock.impl;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import com.lock.model.LockInfo;

public class FairLock extends AbstractLock {
    public FairLock(RedissonClient redissonClient, LockInfo lockInfo) {
        this.redissonClient = redissonClient;
        this.lockInfo = lockInfo;
    }

    @Override
    protected RLock getLock(String name) {
        return redissonClient.getFairLock(name);
    }
}
