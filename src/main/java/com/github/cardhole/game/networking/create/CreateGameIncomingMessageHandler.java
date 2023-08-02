package com.github.cardhole.game.networking.create;

import com.github.cardhole.deck.service.RandomDeckFactory;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.networking.GameNetworkingManipulator;
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
    private final RandomDeckFactory randomDeckFactory;
    private final HomeRefresherService homeRefresherService;
    private final GameNetworkingManipulator gameNetworkingManipulator;

    @Override
    public void handleMessage(final Session session, final CreateGameIncomingMessage message) {
        final Game newGame = gameRegistry.createGame(message.name());
        final Player newPlayer = newGame.createPlayer(session, randomDeckFactory.buildRandomDeck());

        session.setActiveGameId(newGame.getId());

        gameNetworkingManipulator.sendMessageToPlayer(newPlayer,
                JoinGameOutgoingMessage.builder()
                        .name(message.name())
                        .players(
                                List.of(
                                        JoinGameOutgoingMessage.Player.builder()
                                                .id(newPlayer.getId())
                                                .name(session.getName())
                                                .myPlayer(true)
                                                .deckSize(newPlayer.getCardCountInDeck())
                                                .life(newPlayer.getLife())
                                                .build()
                                )
                        )
                        .build()
        );
        gameNetworkingManipulator.sendStopRefresh(newPlayer);
        gameNetworkingManipulator.sendGameMessageToPlayer(newPlayer, "Waiting for an opponent to join.");

        homeRefresherService.refreshHomeForSessions();
    }

    @Override
    public Class<CreateGameIncomingMessage> supportedMessage() {
        return CreateGameIncomingMessage.class;
    }
}
