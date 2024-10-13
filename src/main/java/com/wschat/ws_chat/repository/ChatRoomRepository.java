package com.wschat.ws_chat.repository;

import com.wschat.ws_chat.dto.ChatRoom;
import com.wschat.ws_chat.pubsub.RedisSubscriber;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Repository;

import java.util.*;

@RequiredArgsConstructor // final 필드를 포함한 생성자를 자동으로 생성하는 어노테이션
@Repository // 이 클래스를 Spring의 레포지토리 빈으로 등록하여 의존성 주입 가능하게 만듭니다.
public class ChatRoomRepository { // ChatRoom 데이터를 관리하는 레포지토리 클래스입니다.
    private final RedisMessageListenerContainer redisMessageListener; // Redis 메시지 리스너 컨테이너
    private final RedisSubscriber redisSubscriber; // Redis 메시지를 구독하는 RedisSubscriber 인스턴스

    private static final String CHAT_ROOMS = "CHAT_ROOM"; // Redis 해시 키로 사용할 상수
    private final RedisTemplate<String, Object> redisTemplate; // Redis 작업을 위한 RedisTemplate
    private HashOperations<String, String, ChatRoom> opsHashChatRoom; // Redis 해시 연산을 위한 HashOperations
    private Map<String, ChannelTopic> topics; // 채팅방 ID별 ChannerTopic을 저장하는 Map

    @PostConstruct // Bean이 초기화된 후 호출되는 초기화 메서드
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash(); // Redis 해시 연산을 초기화
        topics = new HashMap<>(); // 채팅방 토픽을 저장할 HashMap 초기화
    }

    // 모든 채팅방 목록을 반환하는 메서드
    public List<ChatRoom> findAllRoom() {
        return opsHashChatRoom.values(CHAT_ROOMS); // Redis 해시에서 모든 채팅방 값을 가져옴
    }

    // ID로 특정 채팅방을 찾아 반환하는 메서드
    public ChatRoom findRoomById(String id) {
        return opsHashChatRoom.get(CHAT_ROOMS, id); // 주어진 ID로 채팅방을 찾아 반환
    }

    // 주어진 이름으로 새로운 채팅방을 생성하는 메서드
    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name); // 주어진 이름으로 새로운 채팅방을 생성합니다.
        opsHashChatRoom.put(CHAT_ROOMS, chatRoom.getRoomId(), chatRoom); // Redis 해시에 새로운 채팅방 저장
        return chatRoom; // 생성된 채팅방 객체를 반환합니다.
    }

    // 특정 채팅방에 사용자가 입장할 때 실행되는 메서드
    public void enterChatRoom(String roomId) {
        ChannelTopic topic = topics.get(roomId); // 주어진 roomId에 해당하는 Topic을 가져옴
        if (topic == null) { // 해당 roomId에 Topic이 없으면
            topic = new ChannelTopic(roomId); // 새로운 ChannelTopic 생성
            redisMessageListener.addMessageListener(redisSubscriber, topic); // Redis 리스너에 구독자로 등록
            topics.put(roomId, topic); // Map에 roomId와 Topic 추가
        }
    }

    // 주어진 roomId에 해당하는 ChannelTopic을 반환하는 메서드
    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId); // topics 맵에서 roomId에 해당하는 Topic 반환
    }
}