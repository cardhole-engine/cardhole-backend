package com.github.cardhole.game.networking.create;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.networking.create.domain.CreateGameIncomingMessage;
import com.github.cardhole.game.networking.join.domain.JoinGameOutgoingMessage;
import com.github.cardhole.game.service.container.GameContainer;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.networking.domain.Session;
import com.github.cardhole.player.domain.Player;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CreateGameIncomingMessageHandler implements MessageHandler<CreateGameIncomingMessage> {

    private final GameContainer gameContainer;

    @Override
    public void handleMessage(final Session session, final CreateGameIncomingMessage message) {
        gameContainer.registerGame(new Game(message.name(), new Player(session)));

        session.setInGame(true);
        session.sendMessage(
                JoinGameOutgoingMessage.builder()
                        .name(message.name())
                        .players(
                                List.of(
                                        JoinGameOutgoingMessage.Player.builder()
                                                .name(session.getName())
                                                .build()
                                )
                        )
                        .build()
        );
    }

    @Override
    public Class<CreateGameIncomingMessage> supportedMessage() {
        return CreateGameIncomingMessage.class;
    }
}
