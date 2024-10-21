package com.wschat.ws_chat.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter // getter 메서드를 자동 생성
@Setter // setter 메서드를 자동 생성
public class ChatMessage { // 채팅 메시지 정보를 담는 DTO 클래스

    // 기본 생성자
    public ChatMessage() {
    }

    @Builder // 빌더 패턴을 적용
    public ChatMessage(MessageType type, String roomId, String sender, String message, long userCount) {
        this.type = type; // 메시지 타입 초기화
        this.roomId = roomId; // 채팅방 ID 초기화
        this.sender = sender; // 발신자 초기화
        this.message = message; // 메시지 내용 초기화
        this.userCount = userCount; // 채팅방 인원 수 초기화
    }

    // 메시지 타입을 정의하는 enum 클래스
    public enum MessageType {
        ENTER, QUIT, TALK
    }

    private MessageType type; // 메시지 타입 (입장, 퇴장, 대화)
    private String roomId; // 채팅방 ID
    private String sender; // 메시지 보낸사람의 닉네임
    private String message; // 메시지 내용
    private long userCount; // 채팅방 인원수
}
