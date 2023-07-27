package com.github.cardhole.game.networking.join;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.networking.GameNetworkingManipulator;
import com.github.cardhole.game.networking.join.domain.JoinGameOutgoingMessage;
import com.github.cardhole.game.networking.join.domain.PlayerJoinedOutgoingMessage;
import com.github.cardhole.game.networking.join.domain.RequestJoinIncomingMessage;
import com.github.cardhole.game.service.GameManager;
import com.github.cardhole.game.service.container.GameRegistry;
import com.github.cardhole.home.service.HomeRefresherService;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.session.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestJoinIncomingMessageHandler implements MessageHandler<RequestJoinIncomingMessage> {

    private final GameManager gameManager;
    private final GameRegistry gameRegistry;
    private final HomeRefresherService homeRefresherService;
    private final GameNetworkingManipulator gameNetworkingManipulator;

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

        // Initialize the screen for the joined player
        final Player joiningPlayer = new Player(session);

        gameToJoin.joinPlayer(joiningPlayer);

        session.setActiveGameId(gameToJoin.getId());
        session.sendMessage(
                JoinGameOutgoingMessage.builder()
                        .name(gameToJoin.getName())
                        .players(
                                gameToJoin.getPlayers().stream()
                                        .map(player -> JoinGameOutgoingMessage.Player.builder()
                                                .name(player.getName())
                                                .myPlayer(player.getSession().equals(session))
                                                .build()
                                        )
                                        .toList()
                        )
                        .build()
        );

        // Refresh the game screen for the opponents
        gameNetworkingManipulator.broadcastMessageExceptTo(gameToJoin, joiningPlayer,
                "Player " + session.getName() + " joined the game!");
        gameNetworkingManipulator.sendToEveryoneExceptTo(gameToJoin, joiningPlayer,
                PlayerJoinedOutgoingMessage.builder()
                        .name(session.getName())
                        .build()
        );

        // Start the game, we only support 2 players at the moment
        if (gameToJoin.getPlayers().size() == 2) {
            gameManager.startGame(gameToJoin);
        }

        // Refresh the lobby screen for everyone in the lobby
        homeRefresherService.refreshHomeForSessions();
    }

    @Override
    public Class<RequestJoinIncomingMessage> supportedMessage() {
        return RequestJoinIncomingMessage.class;
    }
}
