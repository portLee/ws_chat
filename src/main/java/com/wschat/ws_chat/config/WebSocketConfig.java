package com.wschat.ws_chat.config;

import com.wschat.ws_chat.config.handler.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@RequiredArgsConstructor // final 필드에 대해 생성자를 자동으로 생성하는 Lombok 어노테이션
@Configuration // 이 클래스를 Spring 설정 파일로 인식하게 함
@EnableWebSocketMessageBroker // WebSocket 메시지 브로커를 활성화하여 STOMP 프로토콜을 사용할 수 있게 함
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer { // WebSocketMessageBrokerConfigurer 인터페이스를 구현

    private final StompHandler stompHandler; // STOMP 핸들러, WebSocket 메시지를 처리하기 위한 클래스

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) { // 메시지 브로커를 설정하는 메서드
        config.enableSimpleBroker("/sub"); // 클라이언트로부터 구독 요청을 받을 경로 설정, "/sub" 경로로 메시지 발송
        config.setApplicationDestinationPrefixes("/pub"); // 서버로 메시지를 보낼 때 "/pub" 경로 접두사 사용
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) { // STOMP 엔드포인트를 등록하는 메서드
        registry.addEndpoint("/ws-stomp") // WebSocket 연결을 위한 엔드포인트 설정, "/ws-stomp" 경로로 WebSocket 연결
                .setAllowedOriginPatterns("*") // 모든 출처에서의 요청을 허용 (CORS 설정)
                .withSockJS(); // WebSocket을 지원하지 않는 브라우저를 위해 SockJS 폴백(fallback) 사용
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) { // 클라이언트 인바운드 채널 설정
        registration.interceptors(stompHandler); // STOMP 핸들러를 인터셉터로 등록, WebSocket 연결 시 토큰 검증
    }
}
