package org.battlemap.battlemapbe.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Bean
    public ThreadPoolTaskScheduler wsTaskScheduler() {
        ThreadPoolTaskScheduler s = new ThreadPoolTaskScheduler();
        s.setPoolSize(2);
        s.setThreadNamePrefix("ws-heartbeat-");
        s.initialize();
        return s;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 로컬: ws://localhost:8080/ws , 운영: wss://api.battlemap.app/ws
        registry.addEndpoint("/ws")
                // 🔥 모든 오리진 허용 (테스트용)
                .setAllowedOriginPatterns("*")
                .withSockJS();  // SockJS를 쓸 거면 유지, 아니면 지워도 됨

        // ✅ 순수 WebSocket용 (Postman/WebSocket King에서 사용)
        registry.addEndpoint("/ws")
                .setAllowedOriginPatterns("*");

        // 🧪 필요하면 SockJS 클라이언트용도 병행
        registry.addEndpoint("/ws-sockjs")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue")
                .setTaskScheduler(wsTaskScheduler())
                .setHeartbeatValue(new long[]{10_000, 10_000}); // server 10s
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }
}
