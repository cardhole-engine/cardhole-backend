package com.github.cardhole.game.service;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.GameStatus;
import com.github.cardhole.game.networking.GameNetworkingManipulator;
import com.github.cardhole.game.networking.message.domain.ShowDualQuestionGameMessageOutgoingMessage;
import com.github.cardhole.game.networking.message.domain.ShowSimpleGameMessageOutgoingMessage;
import com.github.cardhole.game.service.container.GameRegistry;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.random.service.RandomCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

//TODO: Such an useless name! Come up with something better!
@Service
@RequiredArgsConstructor
public class GameManager {

    private final GameRegistry gameRegistry;
    private final RandomCalculator randomCalculator;
    private final GameNetworkingManipulator gameNetworkingManipulator;

    public void startGame(final Game game) {
        /*
         * 103.1. At the start of a game, the players determine which one of them will choose who takes the first turn.
         * In the first game of a match (including a single-game match), the players may use any mutually agreeable method
         * (flipping a coin, rolling dice, etc.) to do so. In a match of several games, the loser of the previous game
         * chooses who takes the first turn. If the previous game was a draw, the player who made the choice in that game
         * makes the choice in this game. The player chosen to take the first turn is the starting player. The game’s
         * default turn order begins with the starting player and proceeds clockwise.
         */

        game.setStatus(GameStatus.STARTED);

        final Player winnerPlayer = rollUntilWinner(game);

        game.setStartingPlayer(winnerPlayer);

        winnerPlayer.getSession().sendMessage(
                ShowDualQuestionGameMessageOutgoingMessage.builder()
                        .question("Do you want to go first?")
                        .buttonOneText("Yes")
                        .buttonTwoText("No")
                        .responseOneId("GO_FIRST")
                        .responseTwoId("GO_SECOND")
                        .build()
        );

        gameNetworkingManipulator.sendToEveryoneExceptTo(game, winnerPlayer,
                ShowSimpleGameMessageOutgoingMessage.builder()
                        .message("Waiting for the winner player to decide who go first.")
                        .build()
        );
    }

    public void drawForEveryoneInGame(final Game game, final int amount) {
        game.getPlayers()
                .forEach(drawingPlayer -> drawForPlayer(drawingPlayer, amount));
    }

    /**
     * Draws cards for the player and broadcast the changes to the UI for every player.
     *
     * @param player the player to draw the cards for
     * @param amount the amount of cards to draw
     */
    public void drawForPlayer(final Player player, final int amount) {
        final List<Card> drawnCard = player.drawCards(amount);

        drawnCard.forEach(card -> gameNetworkingManipulator
                .sendNewCardToPlayerHand(player, card));

        gameNetworkingManipulator.broadcastPlayerHandSize(player);
        gameNetworkingManipulator.broadcastPlayerDeckSize(player);
    }

    /**
     * Shuffle back the player's hand into it's deck and broadcast the changes to the UI for every player.
     *
     * @param player the player to shuffle the hand back for
     */
    public void shuffleHandBackToDeck(final Player player) {
        player.shuffleHandBackToDeck()
                .forEach(cardId -> gameNetworkingManipulator.sendRemoveCardFromPlayerHand(player, cardId));

        gameNetworkingManipulator.broadcastPlayerHandSize(player);
        gameNetworkingManipulator.broadcastPlayerDeckSize(player);
    }

    /**
     * Initialize mulligan for everyone in the game and broadcast the possibility of the mulligan to the UI od every
     * player.
     *
     * @param game the game to initialize the mulligan in for the players
     */
    public void initializeMulliganForPlayersInGame(final Game game) {
        game.getPlayers()
                .forEach(player -> player.setWaitingForMulliganReply(true));

        gameNetworkingManipulator.sendToEveryone(game,
                ShowDualQuestionGameMessageOutgoingMessage.builder()
                        .question("Do you want to mulligan?")
                        .buttonOneText("Yes")
                        .responseOneId("YES_MULLIGAN")
                        .buttonTwoText("No")
                        .responseTwoId("NO_MULLIGAN")
                        .build()
        );
    }

    public void finishMulliganForPlayer(final Player player) {
        final Game game = gameRegistry.getGame(player.getGameId())
                .orElseThrow();

        gameNetworkingManipulator.resetGameMessageForPlayer(player);

        player.setWaitingForMulliganReply(false);

        boolean gameIsReady = game.getPlayers().stream()
                .noneMatch(Player::isWaitingForMulliganReply);

        if (gameIsReady) {
            gameNetworkingManipulator.broadcastMessage(game, "Mulligan finished for everyone");

            //TODO: begin game
        }
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
}
