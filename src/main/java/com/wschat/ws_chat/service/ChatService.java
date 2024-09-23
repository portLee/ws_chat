package com.wschat.ws_chat.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wschat.ws_chat.dto.ChatRoom;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.*;

@Log4j2 // Log4j2 로깅 기능을 추가하는 어노테이션
@RequiredArgsConstructor // final 필드에 대해 Lombok이 자동으로 생성자를 생성
@Service // 이 클래스를 Spring의 서비스 빈으로 등록
public class ChatService {
    private final ObjectMapper objectMapper; // JSON 직렬화 및 역직렬화를 위한 ObjectMapper 의존성 주입
    private Map<String, ChatRoom> chatRooms; // 채팅방을 관리하는 Map, 채팅방 ID를 키로 사용

    @PostConstruct // 빈 초기화 시 호출되는 메서드로, ChatService가 생성될 때 실행
    private void init() {
        chatRooms = new LinkedHashMap<>(); // 채팅방을 관리하기 위한 LinkedHashMap 초기화
    }                                      // 채팅방 생성된 순서 유지하기위해 LinkedHashMap 사용

    // 모든 채팅방 목록을 반환하는 메서드
    public List<ChatRoom> findAllRoom() {
        return new ArrayList<>(chatRooms.values()); // chatRooms에 저장된 모든 채팅방을 리스트로 반환
    }

    // 주어진 ID로 채팅방을 검색하여 반환하는 메서드
    public ChatRoom findRoomById(String roomId) {
        return chatRooms.get(roomId); // 채팅방 ID로 해당 채팅방을 검색하여 반환
    }

    // 새로운 채팅방을 생성하는 메서드
    public ChatRoom createRoom(String name) {
        String randomId = UUID.randomUUID().toString(); // UUID를 이용해 고유한 채팅방 ID 생성
        ChatRoom chatRoom = ChatRoom.builder() // ChatRoom 빌더 패턴을 사용하여 객체 생성
                .roomId(randomId)
                .name(name)
                .build();
        chatRooms.put(randomId, chatRoom); // 생성한 채팅방을 chatRooms에 추가
        return chatRoom; // 생성된 채팅방을 반환
    }

    // 주어진 세션으로 메시지를 전송하는 메서드
    public <T> void sendMessage(WebSocketSession session, T message) {
        try {
            // ObjectMapper를 사용해 메시지를 JSON 문자열로 변환하고 WebSocket 세션을 통해 전송
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(message)));
        } catch (IOException e) {
            // 예외 발생 시 로그를 출력
            log.error(e.getMessage(), e);
        }
    }
}
