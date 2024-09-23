package com.wschat.ws_chat.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wschat.ws_chat.dto.ChatMessage;
import com.wschat.ws_chat.dto.ChatRoom;
import com.wschat.ws_chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

@Log4j2 // Log4j2를 사용한 로깅 기능을 제공하는 어노테이션
@RequiredArgsConstructor // final 필드에 대해 Lombok이 자동으로 생성자를 생성해주는 어노테이션
@Component // 해당 클래스를 Spring의 빈(Bean)으로 등록하여 컴포넌트로 사용할 수 있게 함
public class WebSocketChatHandler extends TextWebSocketHandler { // TextWebSocketHandler를 상속받아 웹소켓 메시지를 처리하는 클래스 정의
    private final ObjectMapper objectMapper; // JSON 직렬화 및 역직렬화를 위한 ObjectMapper 의존성 주입
    private final ChatService chatService; // 채팅 서비스 로직을 처리하기 위한 ChatService 의존성 주입

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception { // 클라이언트로부터 텍스트 메시지가 수신되었을 때 호출되는 메서드
        String payload = message.getPayload(); // 수신된 메시지의 본문(payload)을 가져옴
        log.info("payload {}", payload); // 수신된 메시지를 로그로 출력
//        TextMessage textMessage = new TextMessage("Welcome chatting server!"); // 클라이언트에게 보낼 환영 메시지를 생성
//        session.sendMessage(textMessage); // 클라이언트로 환영 메시지를 전송
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class); // 수신된 메시지를 ChatMessage 객체로 변환
        ChatRoom room = chatService.findRoomById(chatMessage.getRoomId()); // 채팅방 ID를 기반으로 해당 채팅방을 조회
        room.handleActions(session, chatMessage, chatService); // 채팅방에서 발생한 액션(입장, 메시지 전송 등)을 처리
    }
}
