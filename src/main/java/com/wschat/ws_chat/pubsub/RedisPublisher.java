package com.wschat.ws_chat.pubsub;

import com.wschat.ws_chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor // final 필드를 생성자 주입 방식으로 초기화하는 Lombok 어노테이션
@Service // 이 클래스가 Spring의 서비스 레이어에 속함을 나타냄
public class RedisPublisher {
    // Redis에 메시지를 발행하는 데 사용할 RedisTemplate 객체
    private final RedisTemplate<String, Object> redisTemplate;

    // 특정 채널(topic)에 메시지를 발행하는 메서드
    public void publish(ChannelTopic topic, ChatMessage message) {
        // Redis의 특정 채널에 메시지를 전송
        redisTemplate.convertAndSend(topic.getTopic(), message);
    }
}
