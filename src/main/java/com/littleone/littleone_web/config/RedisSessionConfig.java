package com.littleone.littleone_web.config;

import java.net.URISyntaxException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.security.web.session.HttpSessionEventPublisher;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

@PropertySource("classpath:api_key.properties")
@Configuration
@EnableRedisHttpSession		// 스프링세션을 레디스와 연결해주는 어노테이션
public class RedisSessionConfig {

	@Value("${redis_host}")
	private String redis_host;
	
	@Value("${redis_port}")
	private String redis_port;
	
	@Value("${redis_password}")
	private String redis_password;

	@Bean("jedisConnectionFactory")
    public JedisConnectionFactory connectionFactory() throws URISyntaxException {
        JedisConnectionFactory redis = new JedisConnectionFactory();
        redis.setHostName(redis_host);
        redis.setPort(6379);
        redis.setPassword(redis_password);
        redis.setUsePool(true);
        return redis;
    }
	
	@Bean("redisTemplate")
	public RedisTemplate<String, Object> redisTemplate() throws URISyntaxException {
		RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
		redisTemplate.setKeySerializer(new StringRedisSerializer());
		redisTemplate.setValueSerializer(new StringRedisSerializer());
		//redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
		redisTemplate.setConnectionFactory(connectionFactory());
		redisTemplate.afterPropertiesSet();
		return redisTemplate;
	}
	
	@Bean
	public CookieSerializer cookieSerializer()
	{
		DefaultCookieSerializer serializer = new DefaultCookieSerializer();
		// 위 레디스 처럼 serializer 의 각종 설정 가능.
		// tomcat context 로 설정한 쿠키 기능들도 여기서 설정가능.
		//serializer.setCookieMaxAge(cookieMaxAge);
		//serializer.setUseSecureCookie(useSecureCookie);
		return serializer;
	}
	
	// 중복로그인 방지를 위한 session 리스너를 빈으로 추가
	@Bean
	public CustomHttpSessionListener CustomHttpSessionListener() {
	    return new CustomHttpSessionListener();
	}

}
