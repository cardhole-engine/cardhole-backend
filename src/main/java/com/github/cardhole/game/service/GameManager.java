package com.github.cardhole.game.service;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.GameStatus;
import com.github.cardhole.game.domain.Step;
import com.github.cardhole.game.networking.GameNetworkingManipulator;
import com.github.cardhole.game.networking.message.domain.ShowDualQuestionGameMessageOutgoingMessage;
import com.github.cardhole.game.networking.message.domain.ShowOkGameMessageOutgoingMessage;
import com.github.cardhole.game.networking.message.domain.ShowSimpleGameMessageOutgoingMessage;
import com.github.cardhole.game.service.container.GameRegistry;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.random.service.RandomCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
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

            moveToNextStep(game);
        }
    }

    public void moveToNextStep(final Game game) {
        //At the beginning of the first turn the step is null
        if (game.getStep() == null) {
            game.setTurn(1);
            game.setStep(Step.UNTAP);
        }

        switch (game.getStep()) {
            case UNTAP -> {
                /*
                 * 502. Untap Step
                 *    - 502.1. First, all phased-in permanents with phasing that the active player controls phase out,
                 *          and all phased-out permanents that the active player controlled when they phased out phase
                 *          in. This all happens simultaneously. This turn-based action doesn’t use the stack. See
                 *          rule 702.26, “Phasing.”
                 *    - 502.2. Second, if it’s day and the previous turn’s active player didn’t cast any spells during
                 *          that turn, it becomes night. If it’s night and the previous turn’s active player cast two or
                 *          more spells during that turn, it becomes day. If it’s neither day nor night, this check
                 *          doesn’t happen and it remains neither. This turn-based action doesn’t use the stack. See
                 *          rule 726, “Day and Night.”
                 *    - 502.2a Multiplayer games using the shared team turns option use a modified rule. If it’s day
                 *          and no player on the previous turn’s active team cast a spell during that turn, it becomes
                 *          night. If it’s night and any player on the previous turn’s active team cast two or more
                 *          spells during the previous turn, it becomes day. If it’s neither day nor night, this check
                 *          doesn’t happen and it remains neither. This turn-based action doesn’t use the stack.
                 *    - 502.3. Third, the active player determines which permanents they control will untap. Then they
                 *          untap them all simultaneously. This turn-based action doesn’t use the stack. Normally, all
                 *          of a player’s permanents untap, but effects can keep one or more of a player’s permanents
                 *          from untapping.
                 *    - 502.4. No player receives priority during the untap step, so no spells can be cast or resolve
                 *          and no abilities can be activated or resolve. Any ability that triggers during this step
                 *          will be held until the next time a player would receive priority, which is usually during
                 *          the upkeep step. (See rule 503, “Upkeep Step.”)
                 */
                //TODO: Logic to untap everyting
            }
            case UPKEEP -> {
                /*
                 * 503. Upkeep Step
                 *    - 503.1. The upkeep step has no turn-based actions. Once it begins, the active player gets
                 *          priority. (See rule 117, “Timing and Priority.”)
                 *    - 503.1a Any abilities that triggered during the untap step and any abilities that triggered at
                 *          the beginning of the upkeep are put onto the stack before the active player gets priority;
                 *          the order in which they triggered doesn’t matter. (See rule 603, “Handling Triggered
                 *          Abilities.”)
                 *    - 503.2. If a spell states that it may be cast only “after [a player’s] upkeep step,” and the turn
                 *          has multiple upkeep steps, that spell may be cast any time after the first upkeep step ends.
                 */
                //TODO: Upkeep logic here

                initializePhasePriority(game);
                broadcastPriority(game);
            }
        }
    }

    /**
     * Initialize the priority queue for the active game phase.
     *
     * @param game the game to initialize the queue for
     */
    public void initializePhasePriority(final Game game) {
        final List<Player> playerPriority = new LinkedList<>();

        // The active player starts first
        playerPriority.add(game.getActivePlayer());

        // The opponent goes second
        playerPriority.add(
                game.getPlayers().stream()
                        .filter(player -> !player.equals(game.getActivePlayer()))
                        .findFirst()
                        .orElseThrow()
        );

        game.setPhasePriority(playerPriority);
    }

    /**
     * Moves the priority to the next player, or move the game to the next phase if no-one is left who holds priority
     * for this phase.
     *
     * @param game the game to upgrade the priority for
     */
    public void movePriority(final Game game) {
        if (game.getPriorityPlayer() == null) {
            moveToNextStep(game);

            return;
        }

        game.movePriority();
        broadcastPriority(game);
    }

    /**
     * Broadcast the priority owner to everyone in the game.
     *
     * @param game the game to broadcast the priority in
     */
    public void broadcastPriority(final Game game) {
        game.getPriorityPlayer().getSession().sendMessage(
                ShowOkGameMessageOutgoingMessage.builder()
                        .message("Cast spells and activate abilities.")
                        .buttonId("PASS_PRIORITY")
                        .buttonText("Ok")
                        .build()
        );

        gameNetworkingManipulator.broadcastMessageExceptTo(game, game.getPriorityPlayer(), "Waiting for "
                + game.getActivePlayer().getName() + " to act.");
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
