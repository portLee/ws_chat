package com.wschat.ws_chat.controller;

import com.wschat.ws_chat.dto.ChatMessage;
import com.wschat.ws_chat.pubsub.RedisPublisher;
import com.wschat.ws_chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor // final 필드를 생성자 주입 방식으로 초기화하는 Lombok 어노테이션
@Controller // 이 클래스를 Spring 컨트롤러로 정의하여 STOMP 메시지 처리 메서드를 등록할 수 있도록 함
public class ChatController { // 채팅 메시지를 처리하는 컨트롤러 클래스
    
    private final RedisPublisher redisPublisher; // Redis를 통해 메시지를 발행하는 서비스
    private final ChatRoomRepository chatRoomRepository; // 채팅방 정보를 관리하는 저장소

    @MessageMapping("/chat/message") // 클라이언트에서 "/chat/message"로 전송된 메시지를 처리하는 엔드포인트 정의
    public void message(ChatMessage message) { // 클라이언트가 보낸 ChatMessage를 처리하는 메서드
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            // 메시지 타입이 ENTER일 경우 처리
            chatRoomRepository.enterChatRoom(message.getRoomId()); // 사용자가 채팅방에 입장하는 로직 수행
            message.setMessage(message.getSender() + "님이 입장하셨습니다."); // 입장 메시지를 설정
        }
        // 메시지를 특정 채팅방의 Redis 토픽으로 발행
        redisPublisher.publish(chatRoomRepository.getTopic(message.getRoomId()), message);
    }
}
