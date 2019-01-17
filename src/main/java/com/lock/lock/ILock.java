package com.lock.lock;
public interface ILock {
	
	boolean acquire();

    void release();
}
