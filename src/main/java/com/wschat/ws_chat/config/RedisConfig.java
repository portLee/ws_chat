package com.wschat.ws_chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration // Spring 설정 클래스임을 나타냄
public class RedisConfig {
    @Bean // Redis 메시지 리스너 컨테이너를 생성하는 Bean
    public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer(); // RedisMessageListenerContainer 인스턴스 생성
        container.setConnectionFactory(connectionFactory); // Redis 연결 팩토리 설정
        return container; // 컨테이너 반환
    }

    @Bean // RedisTemplate을 생성하는 Bean으로, Redis에 데이터 저장 및 조회 작업을 수행
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>(); // RedisTemplate 인스턴스 생성
        redisTemplate.setConnectionFactory(connectionFactory); // Redis 연결 팩토리 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer()); // Redis 키를 문자열로 직렬화하도록 설정
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class)); // Redis 값 직렬화 방식을 JSON 직렬화로 설정
        return redisTemplate; // 설정이 완료된 RedisTemplate 반환
    }
}
