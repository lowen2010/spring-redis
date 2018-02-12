package com.lowen;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@Component
public class RedisBean {

	@Autowired
	private JedisPool jedisPool;
	
	/**
	 * 把对象放入hash中
	 * @param key
	 * @param field
	 * @param obj
	 * @param seconds
	 */
	public void hset(String key,String field,Object obj,int seconds) {
		Jedis jedis = jedisPool.getResource();
		jedis.hset(key, field, JSON.toJSONString(obj));
		jedis.expire(key, seconds);
		jedis.close();
	}
	
	/**
	 * 从Hash中获取对象
	 * @param key
	 * @param field
	 * @return
	 */
    public String hget(String key,String field){
        Jedis jedis =jedisPool.getResource();
        String text=jedis.hget(key,field);
        jedis.close();
        return text;
    }
    
    /**
     * 从Hash中获取对象,转换成制定类型
     * @param key
     * @param field
     * @param clazz
     * @return
     */
    public <T> T hget(String key,String field,Class<T> clazz){
        String text=hget(key, field);
        T result=JSON.parseObject(text, clazz);
        return result;
    }
    
    /**
     * 从Hash中删除对象
     * @param key
     * @param field
     */
    public void hdel(String key,String ... field){
        Jedis jedis =jedisPool.getResource();
        jedis.hdel(key,field);
        jedis.close();
    }
    
}
