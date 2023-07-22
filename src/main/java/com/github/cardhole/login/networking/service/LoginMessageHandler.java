package com.github.cardhole.login.networking.service;

import com.github.cardhole.login.networking.domain.message.LoginMessage;
import com.github.cardhole.networking.domain.MessageHandler;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

@Service
public class LoginMessageHandler implements MessageHandler<LoginMessage> {

    @Override
    public void handleMessage(final WebSocketSession webSocketSession, final LoginMessage message) {

    }

    @Override
    public Class<LoginMessage> supportedMessage() {
        return LoginMessage.class;
    }
}
