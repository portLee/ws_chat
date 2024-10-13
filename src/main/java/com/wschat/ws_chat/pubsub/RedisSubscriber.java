package com.wschat.ws_chat.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wschat.ws_chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Log4j2 // Log4js 로깅 설정
@RequiredArgsConstructor // final 필드를 포함한 생성자를 자동으로 생성
@Service // Spring 서비스 클래스 설정
public class RedisSubscriber implements MessageListener { // Redis 메시지를 수신하는 리스너 클래스
    private final ObjectMapper objectMapper; // JSON 데이터를 객체로 변환하기 위한 ObjectMapper 인스턴스
    private final RedisTemplate redisTemplate; // Redis 작업을 위한 RedisTemplate 인스턴스
    private final SimpMessageSendingOperations messagingTemplate; // STOMP 메시지 전송을 위한 SimpMessageSendingOperations

    @Override // Redis에서 메시지를 수신할 때 호출되는 메서드
    public void onMessage(Message message, byte[] pattern) {
        try {
            // Redis에서 수신한 메시지를 문자열로 변환
            String publishMessage = (String) redisTemplate.getStringSerializer().deserialize(message.getBody());
            // 변환된 문자열 메시지를 ChatMessage 객체로 매핑
            ChatMessage roomMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
            // 특정 채팅방의 구독 경로로 메시지 전송
            messagingTemplate.convertAndSend("/sub/chat/room/" + roomMessage.getRoomId(), roomMessage);
        } catch (Exception e) { // 예외 발생 시 처리
            log.error(e.getMessage()); // 예외 메시지 로그로 출력
        }
    }
}
