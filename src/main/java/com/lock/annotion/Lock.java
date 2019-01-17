package com.lock.annotion;



import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.lock.model.LockType;


@Target(value = {ElementType.METHOD})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Lock {
    /**
     * 锁的名称
     * @return
     */
    String name() default "";
    /**
     * 锁类型，默认可重入锁
     * @return
     */
    LockType lockType() default LockType.Reentrant;
    /**
     * 尝试加锁，最多等待时间
     * @return
     */
    long waitTime() default 60;
    /**
     *上锁以后xxx秒自动解锁
     * @return
     */
    long leaseTime() default 60*5;

    /**
     * 自定义业务key
     * @return
     */
     String [] keys() default {};
}
