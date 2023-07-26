package com.github.cardhole.game.networking.create;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.networking.create.domain.CreateGameIncomingMessage;
import com.github.cardhole.game.networking.join.domain.JoinGameOutgoingMessage;
import com.github.cardhole.game.service.container.GameRegistry;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.session.domain.Session;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.home.service.HomeRefresherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateGameIncomingMessageHandler implements MessageHandler<CreateGameIncomingMessage> {

    private final GameRegistry gameRegistry;
    private final HomeRefresherService homeRefresherService;

    @Override
    public void handleMessage(final Session session, final CreateGameIncomingMessage message) {
        gameRegistry.registerGame(new Game(message.name(), new Player(session)));

        session.setInGame(true);
        session.sendMessage(
                JoinGameOutgoingMessage.builder()
                        .name(message.name())
                        .players(
                                List.of(
                                        JoinGameOutgoingMessage.Player.builder()
                                                .name(session.getName())
                                                .myPlayer(true)
                                                .build()
                                )
                        )
                        .build()
        );

        homeRefresherService.refreshHomeForSessions();
    }

    @Override
    public Class<CreateGameIncomingMessage> supportedMessage() {
        return CreateGameIncomingMessage.class;
    }
}
