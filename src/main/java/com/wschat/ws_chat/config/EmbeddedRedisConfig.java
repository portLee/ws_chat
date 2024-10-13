package com.wschat.ws_chat.config;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import redis.embedded.RedisServer;

@Profile("local") // "local" 프로필에서만 활성화되는 설정 클래스임을 지정
@Configuration // Spring 설정 클래스임을 나타냄
public class EmbeddedRedisConfig {

    // application.properties에서 설정된 Redis 포트 값을 주입
    @Value("${spring.data.redis.port}")
    private int redisPort;

    // Embedded Redis 서버를 나타내는 변수
    private RedisServer redisServer;

    // Bean 초기화 후 호출되는 메서드로, Redis 서버를 시작
    @PostConstruct
    public void redisServer() {
        redisServer = new RedisServer(redisPort); // Redis 서버를 지정된 포트로 생성
        redisServer.start(); // Redis 서버 시작
    }

    // Bean 종료 전에 호출되는 메서드로, Redis 서버를 중지
    @PreDestroy
    public void stopRedis() {
        if (redisServer != null) { // Redis 서버가 실행 중이면
            redisServer.stop(); // Redis 서버 중지
        }
    }
}
