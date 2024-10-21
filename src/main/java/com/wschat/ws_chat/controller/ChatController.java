package com.wschat.ws_chat.controller;

import com.wschat.ws_chat.dto.ChatMessage;
import com.wschat.ws_chat.repository.ChatRoomRepository;
import com.wschat.ws_chat.service.ChatService;
import com.wschat.ws_chat.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor // final 필드를 생성자 주입 방식으로 초기화하는 Lombok 어노테이션
@Controller // 이 클래스를 Spring 컨트롤러로 정의하여 STOMP 메시지 처리 메서드를 등록할 수 있도록 함
public class ChatController { // 채팅 메시지를 처리하는 컨트롤러 클래스

    // JWT 토큰을 통해 사용자 정보를 추출하기 위한 JwtTokenProvider 인스턴스
    private final JwtTokenProvider jwtTokenProvider;
    // 채팅방 데이터 관리를 위한 ChatRoomRepository 인스턴스
    private final ChatRoomRepository chatRoomRepository;
    // 채팅 메시지를 관리하는 ChatService 인스턴스
    private final ChatService chatService;

    @MessageMapping("/chat/message") // 클라이언트에서 "/chat/message" 경로로 전송한 메시지를 처리
    public void message(ChatMessage message, @Header("token") String token) { // 클라이언트가 보낸 ChatMessage를 처리하는 메서드
        // JWT 토큰에서 닉네임(사용자명)을 추출
        String nickname = jwtTokenProvider.getUserNameFromJwt(token);
        // 메시지의 발신자를 추출한 닉네임으로 설정
        message.setSender(nickname);
        // 해당 채팅방의 현재 인원 수를 메시지에 세팅
        message.setUserCount(chatRoomRepository.getUserCount(message.getRoomId()));
        // Redis를 통해 WebSocket에 발행된 메시지를 모든 구독자에게 전송
        chatService.sendChatMessage(message);
    }
}
