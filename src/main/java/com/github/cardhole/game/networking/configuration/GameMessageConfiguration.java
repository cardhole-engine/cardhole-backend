package com.github.cardhole.game.networking.configuration;

import com.github.cardhole.game.networking.cast.domain.CastCardIncomingMessage;
import com.github.cardhole.game.networking.create.domain.CreateGameIncomingMessage;
import com.github.cardhole.game.networking.join.domain.RequestJoinIncomingMessage;
import com.github.cardhole.game.networking.message.domain.QuestionResponseIncomingMessage;
import com.github.cardhole.game.networking.stop.domain.ChangeStopIncomingMessage;
import com.github.cardhole.networking.domain.MessageTypeRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameMessageConfiguration {

    @Bean
    public MessageTypeRegister createGameIncomingMessageTypeRegister() {
        return MessageTypeRegister.builder()
                .domainClass(CreateGameIncomingMessage.class)
                .build();
    }

    @Bean
    public MessageTypeRegister requestJoinIncomingMessageTypeRegister() {
        return MessageTypeRegister.builder()
                .domainClass(RequestJoinIncomingMessage.class)
                .build();
    }

    @Bean
    public MessageTypeRegister questionResponseIncomingMessageTypeRegister() {
        return MessageTypeRegister.builder()
                .domainClass(QuestionResponseIncomingMessage.class)
                .build();
    }

    @Bean
    public MessageTypeRegister castCardIncomingMessageTypeRegister() {
        return MessageTypeRegister.builder()
                .domainClass(CastCardIncomingMessage.class)
                .build();
    }

    @Bean
    public MessageTypeRegister changeStopIncomingMessageTypeRegister() {
        return MessageTypeRegister.builder()
                .domainClass(ChangeStopIncomingMessage.class)
                .build();
    }
}
