package org.battlemap.battlemapbe.config;

import lombok.RequiredArgsConstructor;
import org.battlemap.battlemapbe.security.StompJwtAuthChannelInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@RequiredArgsConstructor
public class WebSocketSecurityConfig implements WebSocketMessageBrokerConfigurer {

    private final StompJwtAuthChannelInterceptor jwtInterceptor;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        //registration.interceptors(jwtInterceptor);
    }
}
