package com.wschat.ws_chat.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Log4j2 // Log4j2를 사용하여 로그를 기록하는 어노테이션
@Component // 이 클래스를 Spring의 컴포넌트 빈으로 등록
public class JwtTokenProvider { // JWT 토큰을 생성하고 검증하는 서비스 클래스
    
    // JWT 서명에 사용할 Key 객체 생성 (HS256 알고리즘 사용)
    private Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // 토큰의 유효 시간 (1시간) 설정
    private long tokenValidMilisecond = 1000L * 60 * 60; // 1시간만 토큰 유효

    // 사용자의 이름을 기반으로 JWT 토큰을 생성하는 메서드
    public String generateToken(String name) {
        Date now = new Date(); // 현재 시간
        // JWT 빌더 패턴을 사용하여 토큰을 생성
        return Jwts.builder()
                .setId(name) // 토큰의 식별자로 사용자의 이름 설정
                .setIssuedAt(now) // 토큰 발행 시간 설정
                .setExpiration(new Date(now.getTime() + tokenValidMilisecond)) // 토큰 만료 시간 설정
                .signWith(key) // 서명을 위한 키 설정
                .compact(); // 토큰 문자열로 변환하여 반환
    }

    // JWT 토큰에서 사용자의 이름(ID)을 추출하는 메서드
    public String getUserNameFromJwt(String jwt) {
        return getClaims(jwt).getBody().getId(); // 토큰에서 ID 정보를 추출하여 반환
    }

    // JWT 토큰의 유효성을 확인하는 메서드
    public boolean validateToken(String jwt) {
        // 토큰이 유효하면 true를 반환 (null이 아니면 유효)
        return this.getClaims(jwt) != null;
    }

    // JWT 토큰의 클레임 정보를 가져오는 메서드
    private Jws<Claims> getClaims(String jwt) {
        try {
            // JWT 파서를 빌드하고 토큰을 검증하여 클레임을 추출
            return Jwts.parserBuilder()
                    .setSigningKey(key) // 서명 키를 설정
                    .build() // 파서 빌드
                    .parseClaimsJws(jwt); // 주어진 JWT 문자열을 파싱
        } catch (SignatureException ex) { // 서명이 잘못된 경우 처리
            log.error("Invalid JWT signature"); // 로그에 오류 메시지 기록
            throw ex;
        } catch (MalformedJwtException ex) { // 토큰이 손상되었거나 형식이 잘못된 경우 처리
            log.error("Invalid JWT token");
            throw ex;
        } catch (ExpiredJwtException ex) { // 토큰이 만료된 경우 처리
            log.error("Expired JWT token");
            throw ex;
        } catch (UnsupportedJwtException ex) { // 지원되지 않는 JWT 형식인 경우 처리
            log.error("Unsupported JWT token");
            throw ex;
        } catch (IllegalArgumentException ex) { // 잘못된 인자값이 들어온 경우 처리
            log.error("JWT claims string is empty.");
            throw ex;
        }
    }
}
