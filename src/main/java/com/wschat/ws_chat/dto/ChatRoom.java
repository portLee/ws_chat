package com.wschat.ws_chat.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.UUID;

@Getter // roomId, name 필드에 대해 getter 메서드를 자동 생성
@Setter // roomId, name 필드에 대해 setter 메서드를 자동 생성
public class ChatRoom implements Serializable { // 채팅방을 나타내는 클래스, 직렬화 가능

    private static final long serialVersionUID = 7875898148554024765L;

    private String roomId; // 채팅방 고유 ID
    private String name; // 채팅방 이름

    public static ChatRoom create(String name) { // 채팅방을 생성하는 정적 메서드
        ChatRoom chatRoom = new ChatRoom(); // 새로운 ChatRoom 객체를 생성
        chatRoom.roomId = UUID.randomUUID().toString(); // 고유한 roomId를 UUID로 생성하여 할당
        chatRoom.name = name; // 입력받은 이름으로 채팅방 이름을 설정
        return chatRoom; // 생성된 ChatRoom 객체를 반환
    }
}
