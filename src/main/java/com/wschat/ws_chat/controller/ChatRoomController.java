package com.wschat.ws_chat.controller;

import com.wschat.ws_chat.dto.ChatRoom;
import com.wschat.ws_chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor // final 필드를 생성자 주입 방식으로 초기화하는 Lombok
@Controller // 이 클래스를 Spring 컨트롤러로 정의
@RequestMapping("/chat") // "/chat" 경로로 들어오는 요청을 처리하는 컨트롤러 설정
public class ChatRoomController {
    private final ChatRoomRepository chatRoomRepository; // 채팅방 저장소를 참조하는 필드

    @GetMapping("/room") // "/chat/room" GET 요청을 처리
    public String rooms(Model model) {
        return "/chat/room"; // "chat/room.ftl" 템플릿을 반환하여 채팅방 목록 페이지로 이동
    }
    @GetMapping("/rooms") // "/chat/rooms" GET 요청을 처리
    @ResponseBody // JSON 형식으로 반환
    public List<ChatRoom> room() {
        return chatRoomRepository.findAllRoom(); // 모든 채팅방 목록을 반환
    }

    @PostMapping("/room") // "/chat/room" POST 요청을 처리
    @ResponseBody // JSON 형식으로 반환
    public ChatRoom createRoom(@RequestParam String name) {
        return chatRoomRepository.createChatRoom(name); // 새로운 채팅방을 생성하고 반환
    }

    @GetMapping("/room/enter/{roomId}") // "/chat/room/enter/{roomId}" GET 요청을 처리
    public String roomDetail(Model model, @PathVariable String roomId) {
        model.addAttribute("roomId", roomId); // 모델에 roomId를 추가하여 뷰에서 사용 가능하게 함
        return "/chat/roomdetail"; // "chat/roomdetail.ftl" 템플릿을 반환하여 특정 채팅방 페이지로 이동
    }

    @GetMapping("/room/{roomId}") // "/chat/room/{roomId}" GET 요청을 처리
    @ResponseBody // JSON 형식으로 반환
    public ChatRoom roomInfo(@PathVariable String roomId) {
        return chatRoomRepository.findRoomById(roomId); // 특정 roomId에 해당하는 채팅방 정보를 반환
    }
}
