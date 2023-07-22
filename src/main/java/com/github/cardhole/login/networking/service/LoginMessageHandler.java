package com.github.cardhole.login.networking.service;

import com.github.cardhole.login.networking.domain.message.InitializeHomePageMessage;
import com.github.cardhole.login.networking.domain.message.LoginMessage;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.networking.domain.Session;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LoginMessageHandler implements MessageHandler<LoginMessage> {

    @Override
    public void handleMessage(final Session session, final LoginMessage message) {
        session.sendMessage(
                InitializeHomePageMessage.builder()
                        .games(
                                List.of(
                                        InitializeHomePageMessage.RunningGame.builder()
                                                .name("testgame")
                                                .actualPlayers(1)
                                                .maximumPlayers(2)
                                                .build()
                                )
                        )
                        .build()
        );
    }

    @Override
    public Class<LoginMessage> supportedMessage() {
        return LoginMessage.class;
    }
}
