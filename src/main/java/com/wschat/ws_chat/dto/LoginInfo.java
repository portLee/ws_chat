package com.wschat.ws_chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter // name과 token 필드에 대한 getter 메서드를 자동 생성
public class LoginInfo { // 사용자 로그인 정보를 담는 DTO 클래스
    private String name; // 사용자의 이름
    private String token; // 사용자의 JWT 토큰

    @Builder // 빌더 패턴을 사용하여 객체 생성 시 유연하게 값을 설정할 수 있도록 함
    public LoginInfo(String name, String token) { // 생성자, 빌더를 통해 name과 token 필드를 초기화
        this.name = name; // name 필드를 생성자 인자로 받은 값으로 초기화
        this.token = token; // token 필드를 생성자 인자로 받은 값으로 초기화
    }
}
