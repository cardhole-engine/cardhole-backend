package com.github.cardhole.login.networking.service;

import com.github.cardhole.game.service.container.GameContainer;
import com.github.cardhole.login.networking.domain.message.InitializeHomePageOutgoingMessage;
import com.github.cardhole.login.networking.domain.message.LoginIncomingMessage;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.networking.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginMessageHandler implements MessageHandler<LoginIncomingMessage> {

    private final GameContainer gameContainer;

    @Override
    public void handleMessage(final Session session, final LoginIncomingMessage message) {
        session.setName(message.name());

        session.sendMessage(
                InitializeHomePageOutgoingMessage.builder()
                        .games(
                                gameContainer.listGames().stream()
                                        .map(game -> InitializeHomePageOutgoingMessage.RunningGame.builder()
                                                .id(game.getId())
                                                .name(game.getName())
                                                .actualPlayers(game.getPlayerCount())
                                                .maximumPlayers(2)
                                                .build()
                                        )
                                        .toList()
                        )
                        .build()
        );
    }

    @Override
    public Class<LoginIncomingMessage> supportedMessage() {
        return LoginIncomingMessage.class;
    }
}
