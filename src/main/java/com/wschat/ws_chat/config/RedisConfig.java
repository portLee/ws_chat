package com.wschat.ws_chat.config;

import com.wschat.ws_chat.pubsub.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@RequiredArgsConstructor // final 필드를 포함한 생성자를 자동으로 생성하는 Lombok 어노테이션
@Configuration // 이 클래스를 Spring나타냄
public class RedisConfig { // Redis 관련 설정을 담은 클래스
    
    @Bean // 단일 Topic 사용을 위한 Bean 설정
    public ChannelTopic channelTopic() {
        // "chatroom"이라는 이름의 Redis 채널 토픽을 반환
        return new ChannelTopic("chatroom");
    }
    
    @Bean // Redis 메시지 리스너 컨테이너를 생성하는 Bean
    public RedisMessageListenerContainer redisMessageListener(RedisConnectionFactory connectionFactory,
                                                              MessageListenerAdapter listenerAdapter,
                                                              ChannelTopic channelTopic) {
        // RedisMessageListenerContainer 인스턴스 생성
        RedisMessageListenerContainer container = new RedisMessageListenerContainer(); // RedisMessageListenerContainer 인스턴스 생성
        // Redis 연결 팩토리 설정 (Redis와의 연결을 처리)
        container.setConnectionFactory(connectionFactory);
        // 메시지 리스너와 채널 토픽을 리스너 컨테이너에 추가
        container.addMessageListener(listenerAdapter, channelTopic);
        // 설정이 완료된 리스너 컨테이너 반환
        return container;
    }

    @Bean // Redis 메시지를 처리하는 subscriber 설정을 추가하는 Bean
    public MessageListenerAdapter listenerAdapter(RedisSubscriber subscriber) {
        // RedisSubscriber에서 메시지를 처리하기 위한 어댑터 설정
        return new MessageListenerAdapter(subscriber, "sendMessage");
    }

    @Bean // RedisTemplate을 설정하는 Bean, Redis 데이터 저장 및 조회 작업을 처리
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        // RedisTemplate 인스턴스 생성
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        // Redis 연결 팩토리 설정 (Redis 서버와 연결을 담당)
        redisTemplate.setConnectionFactory(connectionFactory);
        // Redis 키를 문자열로 직렬화하도록 설정
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        // Redis 값을 JSON 직렬화로 설정 (String 객체를 JSON 형태로 저장)
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(String.class));
        // 설정이 완료된 RedisTemplate 반환
        return redisTemplate;
    }
}
