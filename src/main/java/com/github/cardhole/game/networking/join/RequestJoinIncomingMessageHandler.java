package com.github.cardhole.game.networking.join;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.networking.join.domain.JoinGameOutgoingMessage;
import com.github.cardhole.game.networking.join.domain.RequestJoinIncomingMessage;
import com.github.cardhole.game.service.container.GameContainer;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.networking.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestJoinIncomingMessageHandler implements MessageHandler<RequestJoinIncomingMessage> {

    private final GameContainer gameContainer;

    @Override
    public void handleMessage(final Session session, final RequestJoinIncomingMessage message) {
        final Optional<Game> gameToJoinOptional = gameContainer.getGame(UUID.fromString(message.gameId()));

        if (gameToJoinOptional.isEmpty()) {
            //TODO: Failed join response
        }

        final Game gameToJoin = gameToJoinOptional.get();

        if (!gameToJoin.hasSpaceForNewJoiner()) {
            //TODO: Failed join response
        }

        session.setInGame(true);
        session.sendMessage(
                JoinGameOutgoingMessage.builder()
                        .name(gameToJoin.getName())
                        .players(
                                gameToJoin.getPlayers().stream()
                                        .map(player -> JoinGameOutgoingMessage.Player.builder()
                                                .name(player.getName())
                                                .build()
                                        )
                                        .toList()
                        )
                        .build()
        );
    }

    @Override
    public Class<RequestJoinIncomingMessage> supportedMessage() {
        return RequestJoinIncomingMessage.class;
    }
}
