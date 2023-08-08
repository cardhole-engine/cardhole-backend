package com.github.cardhole.game.networking.configuration;

import com.github.cardhole.game.networking.ability.domain.UseActivatedAbilityIncomingMessage;
import com.github.cardhole.game.networking.cast.domain.CastCardIncomingMessage;
import com.github.cardhole.game.networking.combat.domain.DeclareAttackerIncomingMessage;
import com.github.cardhole.game.networking.combat.domain.IntendsToBlockWithIncomingMessage;
import com.github.cardhole.game.networking.create.domain.CreateGameIncomingMessage;
import com.github.cardhole.game.networking.join.domain.RequestJoinIncomingMessage;
import com.github.cardhole.game.networking.message.domain.QuestionResponseIncomingMessage;
import com.github.cardhole.game.networking.stop.domain.ChangeStopIncomingMessage;
import com.github.cardhole.networking.domain.MessageTypeRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

//TODO: I don't think this is even needed, we can just autowire all of the message handlers and get the supported message types
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

    @Bean
    public MessageTypeRegister useActivatedAbilityIncomingMessageTypeRegister() {
        return MessageTypeRegister.builder()
                .domainClass(UseActivatedAbilityIncomingMessage.class)
                .build();
    }

    @Bean
    public MessageTypeRegister declareAttackerIncomingMessageTypeRegister() {
        return MessageTypeRegister.builder()
                .domainClass(DeclareAttackerIncomingMessage.class)
                .build();
    }

    @Bean
    public MessageTypeRegister intendsToBlockWithIncomingMessageTypeRegister() {
        return MessageTypeRegister.builder()
                .domainClass(IntendsToBlockWithIncomingMessage.class)
                .build();
    }
}
