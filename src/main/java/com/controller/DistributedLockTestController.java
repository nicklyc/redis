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
	@GetMapping("test1")
	public synchronized String test1(String userCount){
			 testService.handle(userCount);
			 
		    return "OK";
	}
	
	
	@GetMapping("test2")
	public String test2(String userCount){
	long millis = System.currentTimeMillis();
			 testService.handle2(userCount);
		    return "OK";
	}
}
