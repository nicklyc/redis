package com.lock.lock;

import org.junit.Test;

import com.lock.annotion.Lock;

public class AopTest {
	
@Test
public  void main() {
	run();
}



@Lock
public String  run(){
	System.out.println("run");
	return "hello";
	
}
}
