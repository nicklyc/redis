package com.lock.impl;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;

import com.lock.lock.ILock;
import com.lock.model.LockInfo;

import java.util.concurrent.TimeUnit;

public abstract class AbstractLock implements ILock {
    protected RLock rLock;

    protected LockInfo lockInfo;

    protected RedissonClient redissonClient;

    @Override
    public boolean acquire() {
        try {
            rLock = getLock(lockInfo.getName());
            return rLock.tryLock(lockInfo.getWaitTime(), lockInfo.getLeaseTime(), TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            return false;
        }
    }

    @Override
    public void release() {
        if(rLock.isHeldByCurrentThread()){
            rLock.unlockAsync();
        }

    }

    protected abstract RLock getLock(String name);
    public String getKey(){
        return this.lockInfo.getName();
    }



    public RLock getrLock() {
        return rLock;
    }

    public void setrLock(RLock rLock) {
        this.rLock = rLock;
    }

    public LockInfo getLockInfo() {
        return lockInfo;
    }

    public void setLockInfo(LockInfo lockInfo) {
        this.lockInfo = lockInfo;
    }

    public RedissonClient getRedissonClient() {
        return redissonClient;
    }

    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }



}
