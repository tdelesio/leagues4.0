package com.makeurpicks.config;

import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.makeurpicks.domain.Pick;
import com.makeurpicks.repository.DoublePickRepository;
import com.makeurpicks.repository.PickRepository;
import com.makeurpicks.repository.PicksByWeekRepository;
import com.makeurpicks.repository.redis.RedisDoublePlckRepository;
import com.makeurpicks.repository.redis.RedisPickRepository;
import com.makeurpicks.repository.redis.RedisPicksByWeekRepository;

@Configuration
public class RedisConfig {

	@Bean
	public RedisTemplate<String, Pick> pickRedisTemplate(
			RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Pick> template = new RedisTemplate<String, Pick>();
		template.setConnectionFactory(redisConnectionFactory);
		
		template.setHashKeySerializer(stringRedisSerializer());
		template.setKeySerializer(stringRedisSerializer());
		template.setHashValueSerializer(pickJsonRedisSerializer());
		
		return template;
	}  
	
	@Bean
	public RedisTemplate<String, String> doublePickRedisTemplate(
			RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, String> template = new RedisTemplate<String, String>();
		template.setConnectionFactory(redisConnectionFactory);
		
		template.setHashKeySerializer(stringRedisSerializer());
		template.setKeySerializer(stringRedisSerializer());
		template.setHashValueSerializer(stringRedisSerializer());
		
		return template;
	}
	
	@Bean
	public RedisTemplate<String, Map<String, Map<String, Map<String, String>>>> picksByWeekRedisTemplate(RedisConnectionFactory redisConnectionFactory)
	{
		RedisTemplate<String, Map<String, Map<String, Map<String, String>>>> template = new RedisTemplate<>();
		template.setConnectionFactory(redisConnectionFactory);
		
		template.setHashKeySerializer(stringRedisSerializer());
		template.setKeySerializer(stringRedisSerializer());
		
		return template;
	}
	
	@Bean
	public StringRedisSerializer stringRedisSerializer()
	{
		return new StringRedisSerializer();
	}
	
	@Bean
	public Jackson2JsonRedisSerializer<Pick> pickJsonRedisSerializer()
	{
		return new Jackson2JsonRedisSerializer<>(Pick.class);
	}
	
	@Bean
	public PickRepository pickRepository(RedisConnectionFactory redisConnectionFactory)
	{
		PickRepository pickRepository = new RedisPickRepository(pickRedisTemplate(redisConnectionFactory));
		return pickRepository;
	}
	
	@Bean
	public DoublePickRepository doublePickRepository(RedisConnectionFactory redisConnectionFactory)
	{
		DoublePickRepository doublePickRepository = new RedisDoublePlckRepository(doublePickRedisTemplate(redisConnectionFactory));
		return doublePickRepository;
	}
	
	@Bean
	public PicksByWeekRepository picksByWeekRepository(RedisConnectionFactory redisConnectionFactory)
	{
		PicksByWeekRepository picksByWeekRepository = new RedisPicksByWeekRepository(picksByWeekRedisTemplate(redisConnectionFactory));
		return picksByWeekRepository;
	}
}
