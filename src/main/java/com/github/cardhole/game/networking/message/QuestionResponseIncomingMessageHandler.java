package com.github.cardhole.game.networking.message;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.networking.GameNetworkingManipulator;
import com.github.cardhole.game.networking.domain.CheatingException;
import com.github.cardhole.game.networking.message.domain.QuestionResponseIncomingMessage;
import com.github.cardhole.game.service.GameManager;
import com.github.cardhole.game.service.container.GameRegistry;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.session.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class QuestionResponseIncomingMessageHandler implements MessageHandler<QuestionResponseIncomingMessage> {

    private final GameManager gameManager;
    private final GameRegistry gameRegistry;
    private final GameNetworkingManipulator gameNetworkingManipulator;

    @Override
    public void handleMessage(final Session session, final QuestionResponseIncomingMessage message) {
        final Game game = session.getActiveGameId()
                .flatMap(gameRegistry::getGame)
                .orElseThrow();
        final Player player = game.getPlayerForSession(session)
                .orElseThrow();

        switch (message.response()) {
            case "GO_FIRST":
                if (!game.isStartingPlayer(player)) {
                    throw new CheatingException("Player " + player.getName() + " tried to decide who go firs while its"
                            + " not his responsability!");
                }

                game.setActivePlayer(player);
                game.resetStartingPlayer();

                gameNetworkingManipulator.resetGameMessageForEveryone(game);
                gameNetworkingManipulator.broadcastMessage(game, "Player " + player.getName()
                        + " will go first.");

                gameManager.beginningDraw(game);
                //TODO: Ask for mulligan
                break;
            case "GO_SECOND":
                if (!game.isStartingPlayer(player)) {
                    throw new CheatingException("Player " + player.getName() + " tried to decide who go firs while its"
                            + " not his responsability!");
                }

                final Player opponent = game.getPlayers().stream()
                        .filter(player1 -> !player1.equals(player))
                        .findFirst()
                        .orElseThrow();

                game.setActivePlayer(opponent);
                game.resetStartingPlayer();

                gameNetworkingManipulator.resetGameMessageForEveryone(game);
                gameNetworkingManipulator.broadcastMessage(game, "Player " + opponent.getName()
                        + " will go first.");

                gameManager.beginningDraw(game);
                //TODO: Ask for mulligan
                break;
        }
    }

    @Override
    public Class<QuestionResponseIncomingMessage> supportedMessage() {
        return QuestionResponseIncomingMessage.class;
    }
}
