package com.lock.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lock.annotion.Lock;

@RestController
public class HomeController {

	
	@GetMapping("/AOP")
	@Lock
	public String testAop(){
		System.out.println("hello");
		return "hello";
		
	}
}
