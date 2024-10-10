package com.wschat.ws_chat.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.*;

@Configuration // 이 클래스를 Spring 설정 파일로 인식하게 함
@EnableWebSocketMessageBroker // WebSocket 메시지 브로커를 활성화하여 STOMP 프로토콜을 사용할 수 있게 함
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer { // WebSocketMessageBrokerConfigurer 인터페이스를 구현해 WebSocket과 STOMP 설정을 구성

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) { // 메시지 브로커를 설정하는 메서드
        config.enableSimpleBroker("/sub"); // 클라이언트로부터 구독 요청을 받을 경로 설정, "/sub"로 시작하는 경로는 메시지 브로커가 처리
        config.setApplicationDestinationPrefixes("/pub"); // 클라이언트에서 서버로 메시지를 보낼 때 경로의 접두사로 "/pub"을 지정
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) { // STOMP 엔드포인트를 등록하는 메서드
        registry.addEndpoint("/ws-stomp") // WebSocket 연결을 위한 엔드포인트 설정, "/ws-stomp" 경로로 WebSocket 연결
                .setAllowedOriginPatterns("*") // 모든 출처에서의 요청을 허용 (CORS 설정)
                .withSockJS(); // WebSocket을 지원하지 않는 브라우저에서 SockJS 폴백(fallback) 기능을 사용하도록 설정
    }
}
