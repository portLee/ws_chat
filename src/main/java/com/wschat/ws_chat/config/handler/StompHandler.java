package com.wschat.ws_chat.config.handler;

import com.wschat.ws_chat.dto.ChatMessage;
import com.wschat.ws_chat.repository.ChatRoomRepository;
import com.wschat.ws_chat.service.ChatService;
import com.wschat.ws_chat.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Optional;

@Log4j2 // Log4j2를 사용하여 로그를 기록하는 어노테이션
@RequiredArgsConstructor // final 필드들을 생성자 주입 방식으로 초기화하는 Lombok 어노테이션
@Component // 이 클래스를 Spring의 컴포넌트 빈으로 등록
public class StompHandler implements ChannelInterceptor { // 메시지 전송 시 인터셉트하는 핸들러 클래스
    
    // JWT 토큰 검증을 위한 서비스
    private final JwtTokenProvider jwtTokenProvider;
    // 채팅방 데이터를 관리하는 저장소
    private final ChatRoomRepository chatRoomRepository;
    // 채팅 메시지를 전송하는 서비스
    private final ChatService chatService;

    // WebSocket 요청이 처리되기 전에 실행되는 메서드 (메시지 가로채기)
    @Override 
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // STOMP 메시지 헤더에 접근하기 위한 accessor 생성
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        
        // WebSocket 연결 시 (STOMP CONNECT 명령) 헤더에 있는 JWT 토큰 검증
        if (StompCommand.CONNECT == accessor.getCommand()) {
            String jwtToken = accessor.getFirstNativeHeader("token"); // 헤더에서 "token" 값을 가져옴
            log.info("CONNECT {}", jwtToken); // 연결 시 토큰 로그 출력
            jwtTokenProvider.validateToken(jwtToken); // JWT 토큰 검증
        }
        // STOMP SUBSCRIBE 명령 (클라이언트가 채팅방을 구독할 때)
        else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            // 메시지 헤더에서 채팅방 ID 추출
            String roomId = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders().get("simpDestination")).orElse("InvalidRoomId"));
            String sessionId = (String) message.getHeaders().get("simpSessionId"); // 세션 ID 추출
            chatRoomRepository.setUserEnterInfo(sessionId, roomId); // 유저 세션 ID와 채팅방 매핑 정보 저장
            chatRoomRepository.plusUserCount(roomId); // 채팅방 유저 수 증가

            // 메시지 헤더에서 사용자 이름 추출 (없으면 "UnkownUser"로 설정)
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
            // 채팅방 입장 메시지를 생성하고 전송
            chatService.sendChatMessage(ChatMessage.builder().type(ChatMessage.MessageType.ENTER).roomId(roomId).sender(name).build());
            log.info("SUBSCRIBED {}, {}", name, roomId); // 구독 로그 출력
        }
        // STOMP DISCONNECT 명령 (클라이언트가 채팅방에서 나갈 때)
        else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            String sessionId = (String) message.getHeaders().get("simpSessionId"); // 세션 ID 추출
            String roomId = chatRoomRepository.getUserEnterRoomId(sessionId); // 유저가 입장한 채팅방 ID 조회
            chatRoomRepository.minusUserCount(roomId); // 채팅방 유저 수 감소

            // 메시지 헤더에서 사용자 이름 추출 (없으면 "UnknownUser"로 설정)
            String name = Optional.ofNullable((Principal) message.getHeaders().get("simpUser")).map(Principal::getName).orElse("UnknownUser");
            // 채팅방 퇴장 메시지를 생성하고 전송
            chatService.sendChatMessage(ChatMessage.builder().type(ChatMessage.MessageType.QUIT).roomId(roomId).sender(name).build());
            chatRoomRepository.removeUserEnterInfo(sessionId); // 유저 세션 정보 삭제
            log.info("DISCONNECTED {}, {}", sessionId, roomId); // 연결 해제 로그 출력
        }

        return message; // 검증 후 메시지를 그대로 반환하여 계속 처리
    }
}
