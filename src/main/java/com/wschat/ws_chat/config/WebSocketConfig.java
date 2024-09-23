package com.wschat.ws_chat.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@RequiredArgsConstructor // final 필드를 포함하는 생성자를 Lombok이 자동으로 생성해줌
@Configuration // 이 클래스를 Spring 설정 파일로 인식하게 함
@EnableWebSocket // WebSocket을 활성화하여 WebSocket 기능을 사용할 수 있도록 함
public class WebSocketConfig implements WebSocketConfigurer { // WebSocketConfigurer 인터페이스를 구현해 WebSocket을 구성하는 클래스 정의
    private final WebSocketHandler webSocketHandler; // WebSocket 메시지를 처리하는 핸들러를 의존성 주입(WebSocketChatHandler 주입)

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) { // WebSocket 핸들러를 등록하는 메서드
        registry.addHandler(webSocketHandler, "/ws/chat") // "/ws/chat" 경로로 들어오는 WebSocket 연결을 처리하도록 핸들러를 등록
                .setAllowedOrigins("*"); // 모든 출처에서의 요청을 허용 (CORS 설정)
    }
}
