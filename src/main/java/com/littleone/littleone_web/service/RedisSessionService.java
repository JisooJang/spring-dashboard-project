package com.littleone.littleone_web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisSessionService {

	@Autowired
	RedisTemplate<String, String> redisTemplate;
	
	public void delete_session(String sessionId) {
		//redisTemplate.delete("spring:session:expirations:" + sessionId);
		//redisTemplate.delete("spring:session:sessions:expires:" + sessionId);
		//redisTemplate.delete("spring:session:sessions:" + sessionId);
		redisTemplate.delete("spring:session:sessions:"+ sessionId);
	}
	
	public String findByIdx(int member_idx) {
		//redisTemplate.op
		//redisTemplate.
		return "";
	}
}
