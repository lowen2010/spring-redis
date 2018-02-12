package com.lowen;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 新增缓存注解
 * @author lowen
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cacheable {
	 String key();
     String fieldKey() ;
     int expireTime() default 3600;	//过期时间，单位秒，默认1小时
}
