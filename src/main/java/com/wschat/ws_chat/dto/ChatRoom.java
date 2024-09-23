package com.wschat.ws_chat.dto;

import com.wschat.ws_chat.service.ChatService;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;

@Getter // roomId, name, sessions 필드에 대한 getter 메서드를 자동 생성
public class ChatRoom { // 채팅방(ChatRoom) 클래스 정의, WebSocket 세션을 관리하는 역할을 수행
    private String roomId; // 채팅방 고유 ID
    private String name; // 채팅방 이름
    private Set<WebSocketSession> sessions = new HashSet<>(); // 채팅방에 참가한 클라이언트 세션(WebSocketSession)을 저장하는 Set
                                                              // 중복을 방지하기 위해 HashSet 사용

    @Builder // 빌더 패턴을 적용하여 ChatRoom 객체를 쉽게 생성
    public ChatRoom(String roomId, String name) { // ChatRoom 객체의 생성자, roomId와 name을 초기화
        this.roomId = roomId; // 생성된 roomId를 클래스 필드에 저장
        this.name = name; // 생성된 name 클래스 필드에 저장
    }

    // WebSocket을 통해 수신한 메시지를 처리하는 메서드
    public void handleActions(WebSocketSession session, ChatMessage chatMessage, ChatService chatService) {
        if (chatMessage.getType().equals(ChatMessage.MessageType.ENTER)) { // 메시지가 'ENTER' 타입일 때 (사용자가 채팅방에 입장한 경우)
//            System.out.println(session);
            sessions.add(session); // 세션을 참가자 목록에 추가
            chatMessage.setMessage(chatMessage.getSender() + "님이 입장했습니다."); // 입장 메시지를 설정
        }
        sendMessage(chatMessage, chatService); // 메시지를 해당 채팅방에 속한 모든 세션에 전송
    }

    // 채팅 메시지를 채팅방에 속한 모든 WebSocketSession에 전송하는 메서드
    public <T> void sendMessage(T message, ChatService chatService) {
        sessions.parallelStream() // 채팅방의 모든 세션에 대해 병렬 스트림을 사용하여 메시지를 전송
                .forEach(session -> chatService.sendMessage(session, message)); // 각 세션에 대해 ChatService의 sendMessage 메서드를 호출하여 메시지를 전송
    }
}
