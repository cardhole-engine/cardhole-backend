package com.github.cardhole.login.networking.service;

import com.github.cardhole.game.service.container.GameContainer;
import com.github.cardhole.login.networking.domain.message.InitializeHomePageMessage;
import com.github.cardhole.login.networking.domain.message.LoginMessage;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.networking.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginMessageHandler implements MessageHandler<LoginMessage> {

    private final GameContainer gameContainer;

    @Override
    public void handleMessage(final Session session, final LoginMessage message) {
        session.sendMessage(
                InitializeHomePageMessage.builder()
                        .games(
                                gameContainer.listGames().stream()
                                        .map(game -> InitializeHomePageMessage.RunningGame.builder()
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
    public Class<LoginMessage> supportedMessage() {
        return LoginMessage.class;
    }
}
