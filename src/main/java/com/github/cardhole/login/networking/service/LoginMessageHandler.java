package com.github.cardhole.login.networking.service;

import com.github.cardhole.login.networking.domain.message.LoginIncomingMessage;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.session.domain.Session;
import com.github.cardhole.home.service.HomeRefresherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginMessageHandler implements MessageHandler<LoginIncomingMessage> {

    private final HomeRefresherService homeRefresherService;

    @Override
    public void handleMessage(final Session session, final LoginIncomingMessage message) {
        session.setName(message.name());

        homeRefresherService.refreshHomeForSession(session);
    }

    @Override
    public Class<LoginIncomingMessage> supportedMessage() {
        return LoginIncomingMessage.class;
    }
}
