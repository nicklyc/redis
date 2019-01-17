package com.lock.impl;

import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;

import com.lock.model.LockInfo;

public class ReadLock extends AbstractLock {
    public ReadLock(RedissonClient redissonClient, LockInfo lockInfo) {
        this.redissonClient = redissonClient;
        this.lockInfo = lockInfo;
    }


    @Override
    protected RLock getLock(String name) {
        RReadWriteLock rwlock = redissonClient.getReadWriteLock("anyRWLock");
        return  rwlock.readLock();
    }
}
