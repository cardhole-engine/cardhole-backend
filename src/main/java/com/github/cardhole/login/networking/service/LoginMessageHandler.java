package com.github.cardhole.login.networking.service;

import com.github.cardhole.login.networking.configuration.StaticAssetConfigurationProperties;
import com.github.cardhole.login.networking.domain.message.LoginIncomingMessage;
import com.github.cardhole.login.networking.domain.message.LoginResultOutgoingMessage;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.session.domain.Session;
import com.github.cardhole.home.service.HomeRefresherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginMessageHandler implements MessageHandler<LoginIncomingMessage> {

    private final HomeRefresherService homeRefresherService;
    private final StaticAssetConfigurationProperties staticAssetConfigurationProperties;

    @Override
    public void handleMessage(final Session session, final LoginIncomingMessage message) {
        session.setName(message.name());

        session.sendMessage(
                LoginResultOutgoingMessage.builder()
                        .staticAssetLocation(staticAssetConfigurationProperties.location())
                        .build()
        );

        homeRefresherService.refreshHomeForSession(session);
    }

    @Override
    public Class<LoginIncomingMessage> supportedMessage() {
        return LoginIncomingMessage.class;
    }
}
