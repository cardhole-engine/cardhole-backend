package com.github.cardhole.game.service;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.GameStatus;
import com.github.cardhole.game.networking.GameNetworkingManipulator;
import com.github.cardhole.game.networking.message.domain.ShowDualQuestionGameMessage;
import com.github.cardhole.game.networking.message.domain.ShowSimpleGameMessage;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.random.service.RandomCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

//TODO: Such an useless name! Come up with something better!
@Service
@RequiredArgsConstructor
public class GameManager {

    private final RandomCalculator randomCalculator;
    private final GameNetworkingManipulator gameNetworkingManipulator;

    /*
     * 103.1. At the start of a game, the players determine which one of them will choose who takes the first turn.
     * In the first game of a match (including a single-game match), the players may use any mutually agreeable method
     * (flipping a coin, rolling dice, etc.) to do so. In a match of several games, the loser of the previous game
     * chooses who takes the first turn. If the previous game was a draw, the player who made the choice in that game
     * makes the choice in this game. The player chosen to take the first turn is the starting player. The game’s
     * default turn order begins with the starting player and proceeds clockwise.
     */
    public void startGame(final Game game) {
        game.setStatus(GameStatus.STARTED);

        final Player winnerPlayer = rollUntilWinner(game);

        game.setStartingPlayer(winnerPlayer);

        winnerPlayer.getSession().sendMessage(
                ShowDualQuestionGameMessage.builder()
                        .question("Do you want to go first?")
                        .buttonOneText("Yes")
                        .buttonTwoText("No")
                        .responseOneId("GO_FIRST")
                        .responseTwoId("GO_SECOND")
                        .build()
        );

        gameNetworkingManipulator.sendToEveryoneExceptTo(game, winnerPlayer,
                ShowSimpleGameMessage.builder()
                        .message("Waiting for the winner player to decide who go first.")
                        .build()
        );
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
