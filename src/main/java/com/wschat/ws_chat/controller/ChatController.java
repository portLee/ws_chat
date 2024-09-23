package com.wschat.ws_chat.controller;

import com.wschat.ws_chat.dto.ChatRoom;
import com.wschat.ws_chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor // final 필드를 생성자 주입 방식으로 초기화하는 Lombok 어노테이션
@RestController // 해당 클래스를 RESTful 웹 서비스의 컨트롤러로 정의하는 어노테이션
@RequestMapping("/chat") // "/chat" 경로로 들어오는 HTTP 요청을 처리하는 컨트롤러로 매핑
public class ChatController {
    private final ChatService chatService; // ChatService를 주입받아 채팅방 관련 기능을 처리

    // 채팅방을 생성하는 메서드
    @PostMapping // HTTP POST 요청이 "/chat" 경로로 들어오면 이 메서드가 호출됨
    public ChatRoom createRoom(@RequestParam String name) { // 요청 파라미터로 받은 방 이름을 이용해 채팅방 생성
        return chatService.createRoom(name); // ChatService를 이용해 채팅방을 생성하고 반환
    }

    // 모든 채팅방 목록을 조회하는 메서드
    @GetMapping // HTTP GET 요청이 "/chat" 경로로 들어오면 이 메서드가 호출됨
    public List<ChatRoom> findAllRoom() { // 모든 채팅방 목록을 조회하는 메서드
        return chatService.findAllRoom(); // ChatService를 이용해 모든 채팅방 목록을 조회하여 반환
    }
}
