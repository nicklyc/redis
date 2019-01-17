package com.lock.annotion;



import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.lock.impl.ReentrantLock;
import com.lock.lock.ILock;
import com.lock.model.LockInfo;
import com.lock.model.LockType;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Aspect
@Component
//@Slf4j
public class LockAspectInterceptor {
    private static Logger log = LoggerFactory.getLogger(LockAspectInterceptor.class);
	@Autowired
    private RedissonClient redissonClient;
	
	public static final String LOCK_NAME_PREFIX = "lock";
	
    public static final String LOCK_NAME_SEPARATOR = ".";
	
	private ExpressionParser parser = new SpelExpressionParser();
	
	private ParameterNameDiscoverer nameDiscoverer = new DefaultParameterNameDiscoverer();
	
	//@Autowired
   // LockFactory lockFactory;

    @Around(value = "@annotation(lock)")
    public Object around(ProceedingJoinPoint joinPoint, Lock lock) throws Throwable {
    	
    	 MethodSignature signature = (MethodSignature) joinPoint.getSignature();
         //获取锁的类型。默认是可重入
    	 LockType type= lock.lockType();
         String businessKeyName=getKeyName(joinPoint,lock);
         String lockName = LOCK_NAME_PREFIX+LOCK_NAME_SEPARATOR+getName(lock.name(), signature)+businessKeyName;
         log.info("lockName===>"+lockName);
         long waitTime = lock.waitTime();
         long leaseTime =lock.leaseTime();
         LockInfo lockInfo = new LockInfo(type,lockName,waitTime,leaseTime);
    	
     //  ILock ilock = lockFactory.getLock(lockInfo);
         ILock ilock=new ReentrantLock(redissonClient);
	        boolean currentThreadLock = false;
	        try {
	            currentThreadLock = ilock.acquire();
	            if (!currentThreadLock) {
	            	throw new TimeoutException("获取锁资源等待超时");
				}
	            return joinPoint.proceed();
	        } finally {
	            if (currentThreadLock) {
	            	ilock.release();
	            }
	        }
    }
    
    private String getKeyName(ProceedingJoinPoint joinPoint, Lock lock) {
        List<String> keyList = new ArrayList<>();
        //获取切点方法
        Method method = getMethod(joinPoint);
        List<String> definitionKeys = getSpelDefinitionKey(lock.keys(), method, joinPoint.getArgs());
        keyList.addAll(definitionKeys);
        List<String> parameterKeys = getParameterKey(method.getParameters(), joinPoint.getArgs());
        keyList.addAll(parameterKeys);
        return StringUtils.collectionToDelimitedString(keyList,"","-","");
    }
    
    private List<String> getSpelDefinitionKey(String[] definitionKeys, Method method, Object[] parameterValues) {
        List<String> definitionKeyList = new ArrayList<>();
        for (String definitionKey : definitionKeys) {
            if (definitionKey != null && !definitionKey.isEmpty()) {
                EvaluationContext context = new MethodBasedEvaluationContext(null, method, parameterValues, nameDiscoverer);
                String key = parser.parseExpression(definitionKey).getValue(context).toString();
                definitionKeyList.add(key);
            }
        }
        return definitionKeyList;
    }

    private Method getMethod(ProceedingJoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        //
        if (method.getDeclaringClass().isInterface()) {
            try {
                method = joinPoint.getTarget().getClass().getDeclaredMethod(signature.getName(),
                        method.getParameterTypes());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return method;
    }
    
    private List<String> getParameterKey(Parameter[] parameters, Object[] parameterValues) {
        List<String> parameterKey = new ArrayList<>();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].getAnnotation(LockKey.class) != null) {
                LockKey keyAnnotation = parameters[i].getAnnotation(LockKey.class);
                if (keyAnnotation.value().isEmpty()) {
                    parameterKey.add(parameterValues[i].toString());
                } else {
                    StandardEvaluationContext context = new StandardEvaluationContext(parameterValues[i]);
                    String key = parser.parseExpression(keyAnnotation.value()).getValue(context).toString();
                    parameterKey.add(key);
                }
            }
        }
        return parameterKey;
    }
    
    private String getName(String annotationName, MethodSignature signature) {
        if (annotationName.isEmpty()) {
            return String.format("%s.%s", signature.getDeclaringTypeName(), signature.getMethod().getName());
        } else {
            return annotationName;
        }
    }
}
