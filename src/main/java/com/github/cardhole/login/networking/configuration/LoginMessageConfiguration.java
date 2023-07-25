package com.github.cardhole.login.networking.configuration;

import com.github.cardhole.login.networking.domain.message.LoginIncomingMessage;
import com.github.cardhole.networking.domain.MessageTypeRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoginMessageConfiguration {

    @Bean
    public MessageTypeRegister loginMessageTypeRegister() {
        return MessageTypeRegister.builder()
                .domainClass(LoginIncomingMessage.class)
                .build();
    }
}
