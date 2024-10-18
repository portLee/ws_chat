package com.wschat.ws_chat.config.handler;

import com.wschat.ws_chat.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

@Log4j2 // Log4j2를 사용하여 로그를 기록하는 어노테이션
@RequiredArgsConstructor // final 필드들을 생성자 주입 방식으로 초기화하는 Lombok 어노테이션
@Component // 이 클래스를 Spring의 컴포넌트 빈으로 등록
public class StompHandler implements ChannelInterceptor { // 메시지 전송 시 인터셉트하는 핸들러 클래스
    private final JwtTokenProvider jwtTokenProvider; // JWT 토큰 검증을 위한 서비스

    // WebSocket을 통해 들어온 요청이 처리 되기전에 실행되는 메서드
    @Override 
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // STOMP 메시지 헤더에 접근하기 위한 accessor 생성
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        // WebSocket 연결 시 (STOMP CONNECT 명령) 헤더에 있는 JWT 토큰 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            // 헤더에서 "token" 값을 가져와 JWT 토큰 검증
            jwtTokenProvider.validateToken(accessor.getFirstNativeHeader("token"));
        }
        return message; // 검증 후 메시지를 그대로 반환하여 계속 처리
    }
}
