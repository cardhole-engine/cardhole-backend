package com.github.cardhole.game.networking.join;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.GameStatus;
import com.github.cardhole.game.networking.GameNetworkingManipulator;
import com.github.cardhole.game.networking.join.domain.JoinGameOutgoingMessage;
import com.github.cardhole.game.networking.join.domain.PlayerJoinedOutgoingMessage;
import com.github.cardhole.game.networking.join.domain.RequestJoinIncomingMessage;
import com.github.cardhole.game.networking.start.domain.DecideStartOrYieldOutgoingMessage;
import com.github.cardhole.game.service.container.GameRegistry;
import com.github.cardhole.home.service.HomeRefresherService;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.random.service.RandomCalculator;
import com.github.cardhole.session.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RequestJoinIncomingMessageHandler implements MessageHandler<RequestJoinIncomingMessage> {

    private final GameRegistry gameRegistry;
    private final RandomCalculator randomCalculator;
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

        session.setInGame(true);
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

        // Start the game
        if (gameToJoin.getPlayers().size() == 2) {
            gameToJoin.setStatus(GameStatus.STARTED);

            final Player winnerPlayer = rollUntilWinner(gameToJoin);

            gameToJoin.setStartingPlayer(winnerPlayer);

            winnerPlayer.getSession().sendMessage(
                    DecideStartOrYieldOutgoingMessage.builder()
                            .shouldIStart(true)
                            .build()
            );

            gameNetworkingManipulator.sendToEveryoneExceptTo(gameToJoin, winnerPlayer,
                    DecideStartOrYieldOutgoingMessage.builder()
                            .shouldIStart(false)
                            .build()
            );
        }

        // Refresh the lobby screen for everyone in the lobby
        homeRefresherService.refreshHomeForSessions();
    }

    public Player rollUntilWinner(final Game game) {
        final Player playerOne = game.getPlayers().get(0);
        final Player playerTwo = game.getPlayers().get(1);

        final int playerOneRoll = randomCalculator.randomIntBetween(1, 10);
        final int playerTwoRoll = randomCalculator.randomIntBetween(1, 10);

        gameNetworkingManipulator.broadcastMessage(game, playerOne.getName() + " rolled " + playerOneRoll + ".");
        gameNetworkingManipulator.broadcastMessage(game, playerTwo.getName() + " rolled " + playerTwoRoll + ".");

        if (playerOneRoll == playerTwoRoll) {
            gameNetworkingManipulator.broadcastMessage(game, "The two rolls are equal. Rerolling!");

            return rollUntilWinner(game);
        } else if (playerOneRoll > playerTwoRoll) {
            gameNetworkingManipulator.broadcastMessage(game, playerOne.getName() + " will start the game.");

            return playerOne;
        } else {
            gameNetworkingManipulator.broadcastMessage(game, playerTwo.getName() + " will start the game.");

            return playerTwo;
        }
    }

    @Override
    public Class<RequestJoinIncomingMessage> supportedMessage() {
        return RequestJoinIncomingMessage.class;
    }
}
