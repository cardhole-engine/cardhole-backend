package com.github.cardhole.game.networking.configuration;

import com.github.cardhole.game.networking.create.domain.CreateGameMessage;
import com.github.cardhole.networking.domain.MessageTypeRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameMessageConfiguration {

    @Bean
    public MessageTypeRegister createGameMessageTypeRegister() {
        return MessageTypeRegister.builder()
                .domainClass(CreateGameMessage.class)
                .build();
    }
}
