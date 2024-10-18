package com.wschat.ws_chat.repository;

import com.wschat.ws_chat.dto.ChatRoom;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.*;

@RequiredArgsConstructor // final 필드들을 생성자 주입 방식으로 초기화하는 Lombok 어노테이션
@Repository // 이 클래스를 Spring의 레포지토리 빈으로 등록하여 의존성 주입 가능하게 만듦
public class ChatRoomRepository { // ChatRoom 데이터를 관리하는 레포지토리 클래스입니다.

    // Redis 해시 키로 사용할 상수 (채팅방 데이터를 저장할 키)
    private static final String CHAT_ROOMS = "CHAT_ROOM";
    // Redis 서버와 통신하기 위한 RedisTemplate 인스턴스 (key는 String, value는 Object)
    private final RedisTemplate<String, Object> redisTemplate;
    // Redis에서 해시 연산을 처리하기 위한 HashOperations 객체
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;

    @PostConstruct // Spring Bean이 초기화된 후 자동으로 호출되는 초기화 메서드
    private void init() {
        // Redis 해시 연산을 위한 HashOperations 초기화
        opsHashChatRoom = redisTemplate.opsForHash();
    }

    // 모든 채팅방 조회하는 메서드
    public List<ChatRoom> findAllRoom() {
        // Redis 해시에서 모든 채팅방 정보를 조회하여 반환
        return opsHashChatRoom.values(CHAT_ROOMS);
    }

    // ID로 특정 채팅방을 조회하는 메서드
    public ChatRoom findRoomById(String id) {
        // 주어진 ID로 Redis 해시에서 채팅방을 조회하여 반환
        return opsHashChatRoom.get(CHAT_ROOMS, id);
    }

    // 채팅방을 생성하는 메서드: 서버 간 채팅방을 공유하기 위해 Redis 해시에 저장
    public ChatRoom createChatRoom(String name) {
        // 주어진 이름으로 새로운 채팅방 객체를 생성
        ChatRoom chatRoom = ChatRoom.create(name);
        // Redis 해시에 생성된 채팅방을 저장 (키는 채팅방 ID, 값은 ChatRoom 객체)
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        // 생성된 채팅방 객체를 반환
        return chatRoom;
    }
}