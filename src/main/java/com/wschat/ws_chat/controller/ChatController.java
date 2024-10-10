package com.wschat.ws_chat.controller;

import com.wschat.ws_chat.dto.ChatMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor // final 필드를 생성자 주입 방식으로 초기화하는 Lombok 어노테이션
@Controller // 이 클래스를 Spring 컨트롤러로 정의하여 STOMP 메시지 처리 메서드를 등록할 수 있도록 함
public class ChatController { // 채팅 메시지를 처리하는 컨트롤러 클래스
    private final SimpMessageSendingOperations messagingTemplate; // 메시지를 클라이언트로 전송하기 위한 SimpMessageSendingOperations 주입

    @MessageMapping("/chat/message") // 클라이언트에서 "/chat/message"로 전송된 메시지를 처리하는 엔드포인트 정의
    public void message(ChatMessage message) { // 클라이언트가 보낸 ChatMessage를 처리하는 메서드
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) // 메시지 타입이 JOIN일 경우 처리
            message.setMessage(message.getSender() + "님이 입장하셨습니다."); // 입장 메시지를 설정
        messagingTemplate.convertAndSend("/sub/chat/room/" + message.getRoomId(), message); // 지정된 구독 경로로 메시지를 전송, "/sub/chat/room/{roomId}"로 메시지 전송
    }
}
