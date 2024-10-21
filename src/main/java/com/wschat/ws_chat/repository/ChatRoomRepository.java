package com.wschat.ws_chat.repository;

import com.wschat.ws_chat.dto.ChatRoom;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Repository;

import java.util.*;

@RequiredArgsConstructor // final 필드들을 생성자 주입 방식으로 초기화하는 Lombok 어노테이션
@Repository // 이 클래스를 Spring의 레포지토리 빈으로 등록하여 의존성 주입 가능하게 만듦
public class ChatRoomRepository { // ChatRoom 데이터를 관리하는 레포지토리 클래스입니다.

    // Redis 해시 키로 사용할 상수 (채팅방 데이터를 저장할 키)
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅방 저장
    public static final String USER_COUNT = "USER_COUNT"; // 각 채팅방 유저 수
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅방 입장 정보

    @Resource(name = "redisTemplate")
    // Redis에서 해시 연산을 처리하기 위한 HashOperations 객체 (채팅방 저장 및 조회)
    private HashOperations<String, String, ChatRoom> hashOpsChatRoom;
    @Resource(name = "redisTemplate")
    // 유저 세션과 채팅방 입장 정보 매칭을 위한 HashOperations 객체
    private HashOperations<String, String, String> hashOpsEnterInfo;
    @Resource(name = "redisTemplate")
    // Redis 값 연산을 처리하기 위한 ValueOperations 객체 (유저 수 연산에 사용)
    private ValueOperations<String, String> valueOps;

    // 모든 채팅방 조회하는 메서드
    public List<ChatRoom> findAllRoom() {
        // Redis 해시에서 모든 채팅방 정보를 조회하여 반환
        return hashOpsChatRoom.values(CHAT_ROOMS);
    }

    // ID로 특정 채팅방을 조회하는 메서드
    public ChatRoom findRoomById(String id) {
        // 주어진 ID로 Redis 해시에서 채팅방을 조회하여 반환
        return hashOpsChatRoom.get(CHAT_ROOMS, id);
    }

    // 채팅방을 생성하는 메서드: 서버 간 채팅방을 공유하기 위해 Redis 해시에 저장
    public ChatRoom createChatRoom(String name) {
        // 주어진 이름으로 새로운 채팅방 객체를 생성
        ChatRoom chatRoom = ChatRoom.create(name);
        // Redis 해시에 생성된 채팅방을 저장 (키는 채팅방 ID, 값은 ChatRoom 객체)
        hashOpsChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom);
        // 생성된 채팅방 객체를 반환
        return chatRoom;
    }

    // 유저가 입장한 채팅방ID와 유저 세션ID 매핑 정보 저장
    public void setUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId); // 유저 세션 ID와 채팅방 ID를 Redis에 저장
    }

    // 유저 세션을 통해 입장한 채팅방 ID를 조회
    public String getUserEnterRoomId(String sessionId) {
        // 유저 세션ID로 매핑된 채팅방 ID를 Redis에서 조회하여 반환
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }
    
    // 유저 세션정보와 매핑된 채팅방 ID를 삭제
    public void removeUserEnterInfo(String sessionId) {
        // Redis에서 해당 유저 세션 ID와 채팅방 매핑 정보를 삭제
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }

    // 채팅방의 현재 유저수 조회
    public long getUserCount(String roomId) {
        // Redis에서 채팅방의 유저 수를 조회, 값이 없을 경우 0을 반환
        return Long.valueOf(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0"));
    }

    // 채팅방에 입장한 유저수 +1
    public long plusUserCount(String roomId) {
        // Redis에서 채팅방 유저 수를 1 증가시키고 그 값을 반환
        return Optional.ofNullable(valueOps.increment(USER_COUNT + "_" + roomId)).orElse(0L);
    }

    // 채팅방에 입장한 유저수 -1
    public long minusUserCount(String roomId) {
        // Redis에서 채팅방 유저 수를 1 감소시키고, 유저 수가 0이상일 때만 그 값을 반환
        return Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + roomId)).filter(count -> count > 0).orElse(0L);
    }
}