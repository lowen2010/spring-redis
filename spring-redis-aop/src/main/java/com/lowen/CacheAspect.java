package com.lowen;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

/**
 * redis缓存切面
 * @author lowen
 *
 */
@Component
@Aspect
public class CacheAspect {
	@Autowired
	private RedisBean redisBean;
	
	/**
	 * 拦截Cacheable注解，检查是否存在缓存，若存在则从缓存中获取。
	 * @param pjp
	 * @return
	 */
	@Around("@annotation(com.lowen.Cacheable)")
	public Object cache(ProceedingJoinPoint pjp) {
		Object result=null;
		Method method = this.getMethod(pjp);
		Cacheable cacheable=method.getAnnotation(Cacheable.class);
		String fieldKey = this.parseKey(cacheable.fieldKey(), method, pjp.getArgs());
		//获取方法的返回类型
        Class<?> returnType=((MethodSignature)pjp.getSignature()).getReturnType();
        //获取缓存并转换为指定对象
        result= redisBean.hget(cacheable.key(), fieldKey,returnType);
        if(result==null) {
        	try {
				result = pjp.proceed();
				if(null != result) {
					redisBean.hset(cacheable.key(), fieldKey, result, cacheable.expireTime());
				}
			} catch (Throwable e) {
				e.printStackTrace();
			}
        }
		return result;
	}
	
	/**
	 * 拦截CacheEvict注解，删除缓存
	 * @param pjp
	 */
	@Around("@annotation(com.lowen.CacheEvict)")
	public void evict(ProceedingJoinPoint pjp) {
		Method method = this.getMethod(pjp);
		CacheEvict cacheEvict=method.getAnnotation(CacheEvict.class);
		String fieldKey = this.parseKey(cacheEvict.fieldKey(), method, pjp.getArgs());
		redisBean.hdel(cacheEvict.key(), fieldKey);
		try {
			//数据库执行删除
			pjp.proceed();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	/**
     *  获取被拦截方法对象
     *  
     *  MethodSignature.getMethod() 获取的是顶层接口或者父类的方法对象
     *  而缓存的注解在实现类的方法上,所以应该使用反射获取当前对象的方法对象
     *  
     */
    private Method getMethod(ProceedingJoinPoint pjp){
        //获取参数的类型
        Object [] args=pjp.getArgs();
        Class<?> [] argTypes=new Class[pjp.getArgs().length];
        for(int i=0;i<args.length;i++){
            argTypes[i]=args[i].getClass();
        }
        Method method=null;
        try {
            method=pjp.getTarget().getClass().getMethod(pjp.getSignature().getName(),argTypes);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return method;
        
    }
    
    /**
     * 获取缓存的key，key 定义在注解上，支持SPEL表达式
     * @param key
     * @param method
     * @param args
     * @return
     */
    private String parseKey(String key,Method method,Object [] args){
        //获取被拦截方法参数名列表(使用Spring支持类库)
        LocalVariableTableParameterNameDiscoverer u = new LocalVariableTableParameterNameDiscoverer();  
        String [] paraNameArr=u.getParameterNames(method);
        //使用SPEL进行key的解析
        ExpressionParser parser = new SpelExpressionParser(); 
        //SPEL上下文
        StandardEvaluationContext context = new StandardEvaluationContext();
        //把方法参数放入SPEL上下文中
        for(int i=0;i<paraNameArr.length;i++){
            context.setVariable(paraNameArr[i], args[i]);
        }
        return parser.parseExpression(key).getValue(context,String.class);
    }

}
