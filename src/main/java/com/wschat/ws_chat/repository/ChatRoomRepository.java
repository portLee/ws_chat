package com.wschat.ws_chat.repository;

import com.wschat.ws_chat.dto.ChatRoom;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository // 이 클래스를 Spring의 레포지토리 빈으로 등록하여 의존성 주입 가능하게 만듭니다.
public class ChatRoomRepository { // ChatRoom 데이터를 관리하는 레포지토리 클래스입니다.
    private Map<String, ChatRoom> chatRoomMap; // 채팅방 ID를 키로하고, ChatRoom 객체를 값으로 사용하는 Map입니다.

    @PostConstruct // 빈 초기화 시점에서 호출되는 메서드로, chatRoomMap을 초기화 합니다.
    private void init() {
        chatRoomMap = new LinkedHashMap<>(); // LinkedHashMap으로 채팅방을 관리하여 순서를 유지합니다.
    }

    // 모든 채팅방 목록을 반환하는 메서드
    public List<ChatRoom> findAllRoom() {
        List chatRooms = new ArrayList<>(chatRoomMap.values()); // chatRoomMap의 모든 값을 ArrayList로 변환합니다.
        Collections.reverse(chatRooms); // 채팅방 목록의 순서를 뒤집어 최신 채팅방이 가장 먼저 오도록 설정합니다.
        return chatRooms; // 채팅방 목록을 반환합니다.
    }

    // ID로 특정 채팅방을 찾아 반환하는 메서드
    public ChatRoom findRoomById(String id) {
        return chatRoomMap.get(id); // 채팅방 ID로 채팅방을 찾아 반환합니다.
    }

    // 새로운 채팅방을 생성하고, 생성된 채팅방을 저장 및 반환하는 메서드
    public ChatRoom createChatRoom(String name) {
        ChatRoom chatRoom = ChatRoom.create(name); // 주어진 이름으로 새로운 채팅방을 생성합니다.
        chatRoomMap.put(chatRoom.getRoomId(), chatRoom); // 생성된 채팅방을 chatRoomMap에 추가합니다.
        return chatRoom; // 생성된 채팅방 객체를 반환합니다.
    }
}