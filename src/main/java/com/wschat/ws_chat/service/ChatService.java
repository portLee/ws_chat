package com.wschat.ws_chat.service;

import com.wschat.ws_chat.dto.ChatMessage;
import com.wschat.ws_chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor // final 필드에 대해 생성자 주입을 자동으로 생성하는 Lombok 어노테이션
@Service // Spring의 서비스 빈으로 등록
public class ChatService { // 채팅 메시지 전송 및 채팅방 관리를 위한 서비스 클래스

    // Redis Pub/Sub 메시지를 발행할 채널 토픽
    private final ChannelTopic channelTopic;
    // Redis 작업을 처리하기 위한 템플릿
    private final RedisTemplate redisTemplate;
    // 채팅방 관련 데이터를 관리하는 저장소 클래스
    private final ChatRoomRepository chatRoomRepository;

    // 채팅 메시지의 목적지(destination)에서 채팅방 ID를 추출하는 메서드
    public String getRoomId(String destination) {
        // 목적지에서 마지막 '/' 이후의 부분을 채팅방 ID로 사용
        int lastIndex = destination.lastIndexOf('/');
        if (lastIndex != -1) {
            return destination.substring(lastIndex + 1); // 채팅방 ID 반환
        } else {
            return ""; // 유효한 경로가 아닌 경우 빈 문자열 반환
        }
    }

    // 채팅 메시지를 처리하고 Redis로 전송하는 메서드
    public void sendChatMessage(ChatMessage chatMessage) {
        // 채팅방의 현재 유저 수를 메시지에 설정
        chatMessage.setUserCount(chatRoomRepository.getUserCount(chatMessage.getRoomId()));
        
        // 메시지 타입이 ENTER일 경우 입장 메시지를 설정
        if (ChatMessage.MessageType.ENTER.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에 입장했습니다.");
            chatMessage.setSender("[알림]"); // 발신자를 알림으로 변경
        } 
        // 메시지 타입이 QUIT일 경우 퇴장 메시지를 설정
        else if (ChatMessage.MessageType.QUIT.equals(chatMessage.getType())) {
            chatMessage.setMessage(chatMessage.getSender() + "님이 방에서 나갔습니다.");
            chatMessage.setSender("[알림]"); // 발신자를 알림으로 변경
        }

        // Redis의 해당 채널로 채팅 메시지를 발행(Pub/Sub 방식으로 전송)
        redisTemplate.convertAndSend(channelTopic.getTopic(), chatMessage);
    }
}
