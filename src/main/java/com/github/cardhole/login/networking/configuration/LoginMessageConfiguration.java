package com.github.cardhole.login.networking.configuration;

import com.github.cardhole.login.networking.domain.message.LoginMessage;
import com.github.cardhole.networking.domain.MessageTypeRegister;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoginMessageConfiguration {

    @Bean
    public MessageTypeRegister loginMessageTypeRegister() {
        return MessageTypeRegister.builder()
                .type("LOGIN_MESSAGE")
                .domainClass(LoginMessage.class)
                .build();
    }
}
