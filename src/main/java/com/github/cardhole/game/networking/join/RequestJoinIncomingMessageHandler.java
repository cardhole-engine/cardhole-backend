package com.github.cardhole.game.networking.join;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.networking.join.domain.JoinGameOutgoingMessage;
import com.github.cardhole.game.networking.join.domain.PlayerJoinedOutgoingMessage;
import com.github.cardhole.game.networking.join.domain.RequestJoinIncomingMessage;
import com.github.cardhole.game.service.container.GameRegistry;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.session.domain.Session;
import com.github.cardhole.home.service.HomeRefresherService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestJoinIncomingMessageHandler implements MessageHandler<RequestJoinIncomingMessage> {

    private final GameRegistry gameRegistry;
    private final HomeRefresherService homeRefresherService;

    @Override
    public void handleMessage(final Session session, final RequestJoinIncomingMessage message) {
        final Optional<Game> gameToJoinOptional = gameRegistry.getGame(UUID.fromString(message.gameId()));

        if (gameToJoinOptional.isEmpty()) {
            //TODO: Failed join response
        }

        final Game gameToJoin = gameToJoinOptional.get();

        if (!gameToJoin.hasSpaceForNewJoiner()) {
            //TODO: Failed join response
        }

        gameToJoin.getPlayers().add(new Player(session));

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

        // Refresh the game screen for the opponents
        gameToJoin.getPlayers().stream()
                .filter(player -> !player.getSession().equals(session))
                .forEach(player -> player.getSession().sendMessage(
                                PlayerJoinedOutgoingMessage.builder()
                                        .name(session.getName())
                                        .build()
                        )
                );

        // Refresh the lobby screen for everyone in the lobby
        homeRefresherService.refreshHomeForSessions();
    }

    @Override
    public Class<RequestJoinIncomingMessage> supportedMessage() {
        return RequestJoinIncomingMessage.class;
    }
}
