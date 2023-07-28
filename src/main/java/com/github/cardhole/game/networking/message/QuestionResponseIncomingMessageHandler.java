package com.github.cardhole.game.networking.message;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.networking.GameNetworkingManipulator;
import com.github.cardhole.game.networking.domain.CheatingException;
import com.github.cardhole.game.networking.message.domain.QuestionResponseIncomingMessage;
import com.github.cardhole.game.networking.message.domain.ShowDualQuestionGameMessageOutgoingMessage;
import com.github.cardhole.game.service.GameManager;
import com.github.cardhole.game.service.container.GameRegistry;
import com.github.cardhole.networking.domain.MessageHandler;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.session.domain.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
            case "GO_FIRST" -> {
                if (!game.isStartingPlayer(player) || game.isStartingPlayerWasDecided()) {
                    throw new CheatingException("Player " + player.getName() + " tried to decide who go firs while its"
                            + " not his responsibility or it was already decided!");
                }

                game.setActivePlayer(player);
                game.setStartingPlayerWasDecided(true);

                gameNetworkingManipulator.resetGameMessageForEveryone(game);
                gameNetworkingManipulator.broadcastMessage(game, "Player " + player.getName()
                        + " will go first.");

                gameManager.drawForEveryoneInGame(game, 7);
                gameManager.initializeMulliganForPlayersInGame(game);
            }
            case "GO_SECOND" -> {
                if (!game.isStartingPlayer(player) || game.isStartingPlayerWasDecided()) {
                    throw new CheatingException("Player " + player.getName() + " tried to decide who go firs while its"
                            + " not his responsibility or it was already decided!");
                }

                final Player opponent = game.getPlayers().stream()
                        .filter(player1 -> !player1.equals(player))
                        .findFirst()
                        .orElseThrow();
                game.setActivePlayer(opponent);
                game.setStartingPlayerWasDecided(true);

                gameNetworkingManipulator.resetGameMessageForEveryone(game);
                gameNetworkingManipulator.broadcastMessage(game, "Player " + opponent.getName()
                        + " will go first.");

                gameManager.drawForEveryoneInGame(game, 7);
                gameManager.initializeMulliganForPlayersInGame(game);
            }
            case "YES_MULLIGAN" -> {
                if (player.getMulliganCount() == 6) {
                    throw new CheatingException("Player tries to mulligan when he shouldn't be!");
                }
                gameNetworkingManipulator.broadcastMessage(game, player.getName() + " mulligan his hand!");
                gameManager.shuffleHandBackToDeck(player);
                player.setMulliganCount(player.getMulliganCount() + 1);
                gameManager.drawForPlayer(player, 7 - player.getMulliganCount());
                if (player.getMulliganCount() < 6) {
                    player.getSession().sendMessage(
                            ShowDualQuestionGameMessageOutgoingMessage.builder()
                                    .question("Do you want to mulligan?")
                                    .buttonOneText("Yes")
                                    .responseOneId("YES_MULLIGAN")
                                    .buttonTwoText("No")
                                    .responseTwoId("NO_MULLIGAN")
                                    .build()
                    );
                } else {
                    gameNetworkingManipulator.broadcastMessage(game, player.getName() + " ran out of mulligans!");

                    gameManager.finishMulliganForPlayer(player);
                }
            }
            case "NO_MULLIGAN" -> gameManager.finishMulliganForPlayer(player);
            case "PASS_PRIORITY" -> gameManager.movePriority(game);
        }
    }

    @Override
    public Class<QuestionResponseIncomingMessage> supportedMessage() {
        return QuestionResponseIncomingMessage.class;
    }
}
