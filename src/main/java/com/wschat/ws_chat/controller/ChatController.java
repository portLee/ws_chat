package com.wschat.ws_chat.controller;

import com.wschat.ws_chat.dto.ChatMessage;
import com.wschat.ws_chat.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@RequiredArgsConstructor // final 필드를 생성자 주입 방식으로 초기화하는 Lombok 어노테이션
@Controller // 이 클래스를 Spring 컨트롤러로 정의하여 STOMP 메시지 처리 메서드를 등록할 수 있도록 함
public class ChatController { // 채팅 메시지를 처리하는 컨트롤러 클래스

    // Redis 서버와 통신을 위한 RedisTemplate 인스턴스 (key는 String, value는 Object)
    private final RedisTemplate<String, Object> redisTemplate;
    // JWT 토큰으로부터 사용자 정보를 추출하기 위한 JwtTokenProvider
    private final JwtTokenProvider jwtTokenProvider;
    // 채팅 메시지를 전달할 Redis Pub/Sub 채널 주제를 관리하는 ChannelTopic
    private final ChannelTopic channelTopic;

    @MessageMapping("/chat/message") // 클라이언트에서 "/chat/message"로 전송된 메시지를 처리하는 엔드포인트 정의
    public void message(ChatMessage message, @Header("token") String token) { // 클라이언트가 보낸 ChatMessage를 처리하는 메서드
        // JWT 토큰에서 닉네임(사용자명)을 추출
        String nickname = jwtTokenProvider.getUserNameFromJwt(token);
        // 메시지의 발신자를 추출한 닉네임으로 설정
        message.setSender(nickname);
        // 메시지 타입이 'ENTER'일 경우 (사용자가 채팅방에 입장한 상황)
        if (ChatMessage.MessageType.ENTER.equals(message.getType())) {
            // 입장 알림 메시지의 발신자를 "[알림]"으로 설정
            message.setSender("[알림]");
            // 입장한 사용자의 닉네임을 포함한 입장 메시지 설정
            message.setMessage(nickname + "님이 입장하셨습니다.");
        }
        // 설정된 메시지를 Redis 토픽으로 발행 (채팅방에 참여한 사용자들에게 전송)
        redisTemplate.convertAndSend(channelTopic.getTopic(), message);
    }
}
