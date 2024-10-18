package com.wschat.ws_chat.pubsub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wschat.ws_chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Log4j2 // Log4js를 사용하여 로그를 기록하는 어노테이션
@RequiredArgsConstructor // final 필드들을 생성자 주입 방식으로 초기화하는 Lombok 어노테이션
@Service // 이 클래스를 Spring의 서비스 빈으로 등록
public class RedisSubscriber { // Redis 메시지를 수신하는 리스너 클래스

    // JSON 데이터를 객체로 변환하기 위한 ObjectMapper 인스턴스
    private final ObjectMapper objectMapper;
    // STOMP 메시지를 클라이언트에게 전송하기 위한 SimpMessageSendingOperations 인스턴스
    private final SimpMessageSendingOperations messagingTemplate;

    // Redis에서 수신한 메시지를 처리하고 STOMP를 통해 클라이언트에게 전송하는 메서드
    public void sendMessage(String publishMessage) {
        try {
            // 수신한 메시지를 JSON 형태에서 ChatMessage 객체로 매핑
            ChatMessage chatMessage = objectMapper.readValue(publishMessage, ChatMessage.class);
            // 채팅방을 구독한 클라이언트에게 메시지 발송 ("/sub/chat/room/{roomId}" 경로로
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatMessage.getRoomId(), chatMessage);
        } catch (Exception e) { // 예외 처리
            // 오류가 발생한 경우 로그를 기록
            log.error("Exception {}", e);
        }
    }
}
