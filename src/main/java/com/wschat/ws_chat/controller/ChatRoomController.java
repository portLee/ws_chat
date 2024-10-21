package com.wschat.ws_chat.controller;

import com.wschat.ws_chat.dto.ChatRoom;
import com.wschat.ws_chat.dto.LoginInfo;
import com.wschat.ws_chat.repository.ChatRoomRepository;
import com.wschat.ws_chat.service.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor // final 필드를 생성자 주입 방식으로 초기화하는 Lombok 어노테이션
@Controller // 이 클래스를 Spring 컨트롤러로 정의
@RequestMapping("/chat") // "/chat" 경로로 들어오는 요청을 처리하는 컨트롤러 설정
public class ChatRoomController { // 채팅방 관련 요청을 처리하는 컨트롤러 클래스

    // 채팅방 저장소를 참조하는 필드, 채팅방 목록 조회 및 생성 처리
    private final ChatRoomRepository chatRoomRepository;
    // JWT 토큰 관련 처리를 위한 서비스 필드
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/user") // "/chat/user" GET 요청을 처리
    @ResponseBody // 반환 데이터를 JSON 형식으로 변환하여 응답
    public LoginInfo getUserInfo() { // 현재 로그인된 사용자의 정보를 반환하는 메서드
        // 현재 인증된 사용자 정보를 SecurityContextHolder에서 가져옴
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 인증된 사용자의 이름(로그인 아이디)을 가져옴
        String name = auth.getName();
        // 로그인 정보와 JWT 토큰을 포함한 LoginInfo 객체를 빌더 패턴으로 생성하여 반환
        return LoginInfo.builder()
                .name(name)
                .token(jwtTokenProvider.generateToken(name)) // 사용자 이름으로 JWT 토큰 생성
                .build();
    }

    @GetMapping("/room") // "/chat/room" GET 요청을 처리
    public String rooms(Model model) { // 채팅방 목록 페이지로 이동하는 메서드
        return "/chat/room"; // "chat/room.ftl" 템플릿을 반환하여 채팅방 목록 페이지로 이동
    }
    @GetMapping("/rooms") // "/chat/rooms" GET 요청을 처리
    @ResponseBody // 반환 데이터를 JSON 형식으로 변환하여 응답
    public List<ChatRoom> room() { // 모든 채팅방 목록을 반환하는 메서드
        List<ChatRoom> chatRooms = chatRoomRepository.findAllRoom(); // 모든 채팅방 조회
        chatRooms.stream().forEach(room -> room.setUserCount(chatRoomRepository.getUserCount(room.getRoomId()))); // 각 채팅방의 인원 수 설정
        return chatRooms; // 저장소에서 모든 채팅방을 조회하여 반환
    }

    @PostMapping("/room") // "/chat/room" POST 요청을 처리
    @ResponseBody // 반환 데이터를 JSON 형식으로 변환하여 응답
    public ChatRoom createRoom(@RequestParam String name) { // 새로운 채팅방을 생성하는 메서드
        return chatRoomRepository.createChatRoom(name); // 요청으로 받은 이름으로 새로운 채팅방 생성 후 반환
    }

    @GetMapping("/room/enter/{roomId}") // "/chat/room/enter/{roomId}" GET 요청을 처리
    public String roomDetail(Model model, @PathVariable String roomId) { // 특정 채팅방에 입장하는 메서드
        // 뷰에서 사용하기 위해 모델에 roomId 속성 추가
        model.addAttribute("roomId", roomId);
        // "chat/roomdetail.ftl" 템플릿을 반환하여 특정 채팅방 상세 페이지로 이동
        return "/chat/roomdetail";
    }

    @GetMapping("/room/{roomId}") // "/chat/room/{roomId}" GET 요청을 처리
    @ResponseBody // 반환 데이터를 JSON 형식으로 변환하여 응답
    public ChatRoom roomInfo(@PathVariable String roomId) { // 특정 roomId에 해당하는 채팅방 정보를 반환하는 메서드
        return chatRoomRepository.findRoomById(roomId); // 저장소에서 roomId에 채팅방 정보를 조회하여 반환
    }
}
