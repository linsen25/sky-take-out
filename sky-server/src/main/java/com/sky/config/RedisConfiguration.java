package com.sky.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import lombok.extern.slf4j.Slf4j;


@Configuration
@Slf4j
public class RedisConfiguration {

    @Bean
    public RedisTemplate redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        log.info("开始创建redis模版对象");
        RedisTemplate redisTemplate = new RedisTemplate();
        // 设置redis链接工厂对象
        redisTemplate.setConnectionFactory(redisConnectionFactory );
        // 设置序列化器 不设置序列化器再redis的key会乱码
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        return redisTemplate;
    }

}