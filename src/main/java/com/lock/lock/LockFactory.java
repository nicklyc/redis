package com.lock.lock;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.lock.impl.FairLock;
import com.lock.impl.ReadLock;
import com.lock.impl.ReentrantLock;
import com.lock.impl.WriteLock;
import com.lock.model.LockInfo;

@Component
public class LockFactory {
	@Autowired
    private RedissonClient redissonClient;

    public ILock getLock(LockInfo lockInfo){
        switch (lockInfo.getType()) {
            case Reentrant:
                return new ReentrantLock(redissonClient, lockInfo);
            case Fair:
                return new FairLock(redissonClient, lockInfo);
            case Read:
                return new ReadLock(redissonClient, lockInfo);
            case Write:
                return new WriteLock(redissonClient, lockInfo);
            default:
                return new ReentrantLock(redissonClient, lockInfo);
        }
    }

}
