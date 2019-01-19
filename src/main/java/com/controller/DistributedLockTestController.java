package com.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.service.DistributedLockTestService;
/**
 * 压里测试接口
 * @author admin
 *
 */
@RestController
public class DistributedLockTestController {
    @Autowired
	private DistributedLockTestService testService;
	
    /**
	 * synchronized锁测试
	 * @param userCount
	 * @return
	 */
    @GetMapping("test1")
	public synchronized String test1(String userCount){
			 testService.handle(userCount);
			 
		    return "OK";
	}
	
	
	/**
	 * 基于redis的锁V1
	 * @param userCount
	 * @return
	 */
	@GetMapping("test2")
	public String test2(String userCount){
			 testService.handle2(userCount);
		    return "OK";
	}
	
	
	/**
	 * 基于redis的锁V3
	 * @param userCount
	 * @return
	 */
	@GetMapping("test3")
	public String test3(String userCount){
			 testService.handle3(userCount);
		    return "OK";
	}
}
