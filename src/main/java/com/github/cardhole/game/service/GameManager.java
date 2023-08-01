package com.github.cardhole.game.service;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.land.LandCard;
import com.github.cardhole.card.domain.permanent.PermanentCard;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.GameStatus;
import com.github.cardhole.game.domain.Step;
import com.github.cardhole.game.networking.GameNetworkingManipulator;
import com.github.cardhole.game.networking.cast.domain.RefreshCanBeCastAndActivatedListOutgoingMessage;
import com.github.cardhole.game.networking.message.domain.ShowDualQuestionGameMessageOutgoingMessage;
import com.github.cardhole.game.networking.message.domain.ShowSimpleGameMessageOutgoingMessage;
import com.github.cardhole.game.networking.message.domain.ShowSingleQuestionGameMessageOutgoingMessage;
import com.github.cardhole.mana.domain.Mana;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.random.service.RandomCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

//TODO: Such an useless name! Come up with something better!
@Service
@RequiredArgsConstructor
public class GameManager {

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

        gameNetworkingManipulator.sendMessageToEveryoneExceptTo(game, winnerPlayer,
                ShowSimpleGameMessageOutgoingMessage.builder()
                        .message("Waiting for the winner player to decide who go first.")
                        .build()
        );
    }

    public void drawForEveryoneInGame(final Game game, final int amount) {
        game.getPlayers()
                .forEach(drawingPlayer -> drawForPlayer(drawingPlayer, amount));
    }

    public void drawForPlayer(final Player player) {
        drawForPlayer(player, 1);
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

        gameNetworkingManipulator.broadcastLogMessage(player.getGame(), "Player " + player.getName() + " drawn "
                + amount + " cards.");
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

        gameNetworkingManipulator.sendMessageToEveryone(game,
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
        final Game game = player.getGame();

        gameNetworkingManipulator.resetGameMessageForPlayer(player);

        player.setWaitingForMulliganReply(false);

        boolean gameIsReady = game.getPlayers().stream()
                .noneMatch(Player::isWaitingForMulliganReply);

        if (gameIsReady) {
            gameNetworkingManipulator.broadcastLogMessage(game, "Mulligan finished for everyone");

            moveToNextStep(game);
        }
    }

    public void moveToNextStep(final Game game) {
        //At the beginning of the first turn the step is null
        if (game.getStep() == null) {
            moveToFirstTurn(game);
        } else if (game.getStep() == Step.UNTAP) {
            game.setStep(Step.UPKEEP);
        } else if (game.getStep() == Step.UPKEEP) {
            game.setStep(Step.DRAW);
        } else if (game.getStep() == Step.DRAW) {
            game.setStep(Step.PRECOMBAT_MAIN);
        } else if (game.getStep() == Step.PRECOMBAT_MAIN) {
            game.setStep(Step.BEGIN_COMBAT);
        } else if (game.getStep() == Step.BEGIN_COMBAT) {
            game.setStep(Step.ATTACK);
        } else if (game.getStep() == Step.ATTACK) {
            game.setStep(Step.BLOCK);
        } else if (game.getStep() == Step.BLOCK) {
            game.setStep(Step.DAMAGE);
        } else if (game.getStep() == Step.DAMAGE) {
            game.setStep(Step.END_COMBAT);
        } else if (game.getStep() == Step.END_COMBAT) {
            game.setStep(Step.POSTCOMBAT_MAIN);
        } else if (game.getStep() == Step.POSTCOMBAT_MAIN) {
            game.setStep(Step.END);
        } else if (game.getStep() == Step.END) {
            game.setStep(Step.CLEANUP);
        } else if (game.getStep() == Step.CLEANUP) {
            moveToNextTurn(game);
        }

        gameNetworkingManipulator.broadcastStepChangeMessage(game, game.getStep());

        processStep(game);
    }

    public void moveToFirstTurn(final Game game) {
        game.setTurn(1);
        game.setStep(Step.UNTAP);

        gameNetworkingManipulator.broadcastLogMessage(game, "Starting turn " + game.getTurn()
                + ". It is the turn of " + game.getActivePlayer().getName() + ".");
    }

    public void moveToNextTurn(final Game game) {
        //TODO: End of turn effect cleanup

        game.moveToNextTurn();

        gameNetworkingManipulator.broadcastLogMessage(game, "Starting turn " + game.getTurn()
                + ". It is the turn of " + game.getActivePlayer().getName() + ".");
    }

    public void processStep(final Game game) {
        game.getPlayers()
                .forEach(this::resetManaPool);

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

                moveToNextStep(game);
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
            case DRAW -> {
                /*
                 * 504. Draw Step
                 *    - 504.1. First, the active player draws a card. This turn-based action doesn’t use the stack.
                 *    - 504.2. Second, the active player gets priority. (See rule 117, “Timing and Priority.”)
                 */
                if (!(game.getTurn() == 1 && game.isActivePlayer(game.getStartingPlayer()))) {
                    drawForPlayer(game.getActivePlayer());
                }

                initializePhasePriority(game);
                broadcastPriority(game);
            }
            case PRECOMBAT_MAIN -> {
                /*
                 * 505. Main Phase
                 *    - 505.1. There are two main phases in a turn. In each turn, the first main phase (also known as
                 *          the precombat main phase) and the second main phase (also known as the postcombat main
                 *          phase) are separated by the combat phase (see rule 506, “Combat Phase”). The precombat and
                 *          postcombat main phases are individually and collectively known as the main phase.
                 *      - 505.1a Only the first main phase of the turn is a precombat main phase. All other main phases
                 *            are postcombat main phases. This includes the second main phase of a turn in which the
                 *            combat phase has been skipped. It is also true of a turn in which an effect has caused an
                 *            additional combat phase and an additional main phase to be created.
                 *    - 505.2. The main phase has no steps, so a main phase ends when all players pass in succession
                 *          while the stack is empty. (See rule 500.2.)
                 *    - 505.3. First, but only if the players are playing an Archenemy game (see rule 904), the active
                 *          player is the archenemy, and it’s the active player’s precombat main phase, the active
                 *          player sets the top card of their scheme deck in motion (see rule 701.25). This turn-based
                 *          action doesn’t use the stack.
                 *    - 505.4. Second, if the active player controls one or more Saga enchantments and it’s the active
                 *          player’s precombat main phase, the active player puts a lore counter on each Saga they
                 *          control. (See rule 714, “Saga Cards.”) This turn-based action doesn’t use the stack.
                 *    - 505.5. Third, if the active player controls one or more Attractions and it’s the active
                 *          player’s precombat main phase, the active player rolls to visit their Attractions. (See
                 *          rule 701.49, “Roll to Visit Your Attractions.”) This turn-based action doesn’t use the
                 *          stack.
                 *    - 505.6. Fourth, the active player gets priority. (See rule 117, “Timing and Priority.”)
                 *      - 505.6a The main phase is the only phase in which a player can normally cast artifact,
                 *            creature, enchantment, planeswalker, and sorcery spells. The active player may cast these
                 *            spells.
                 *      - 505.6b During either main phase, the active player may play one land card from their hand if
                 *            the stack is empty, if the player has priority, and if they haven’t played a land this
                 *            turn (unless an effect states the player may play additional lands). This action doesn’t
                 *            use the stack. Neither the land nor the action of playing the land is a spell or ability,
                 *            so it can’t be countered, and players can’t respond to it with instants or activated
                 *            abilities. (See rule 305, “Lands.”)
                 */

                initializePhasePriority(game);
                broadcastPriority(game);
            }
            case BEGIN_COMBAT -> {
                initializePhasePriority(game);
                broadcastPriority(game);
            }
            case ATTACK -> {
                initializePhasePriority(game);
                broadcastPriority(game);
            }
            case BLOCK -> {
                initializePhasePriority(game);
                broadcastPriority(game);
            }
            case DAMAGE -> {
                initializePhasePriority(game);
                broadcastPriority(game);
            }
            case END_COMBAT -> {
                initializePhasePriority(game);
                broadcastPriority(game);
            }
            case POSTCOMBAT_MAIN -> {
                initializePhasePriority(game);
                broadcastPriority(game);
            }
            case END -> {
                initializePhasePriority(game);
                broadcastPriority(game);
            }
            case CLEANUP -> {
                initializePhasePriority(game);
                broadcastPriority(game);
            }
        }
    }

    /**
     * Reset the player's mana pool, removing every mana in there. Updates the UI for every player in the game with this
     * information.
     *
     * @param player the player to reset the mana pool for
     */
    public void resetManaPool(final Player player) {
        player.getManaPool().reset();

        //TODO: broadcast it to everyone
    }

    public void addManaToPlayer(final Player player, final List<Mana> mana) {
        player.getManaPool().addMana(mana);

        //TODO: broadcast it to everyone
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
        resetWhatCanBeCastOrActivated(game.getPriorityPlayer());

        game.movePriority();

        if (game.getPriorityPlayer() == null) {
            moveToNextStep(game);

            return;
        }

        broadcastPriority(game);

        refreshWhatCanBeCastOrActivated(game.getPriorityPlayer());
    }

    /**
     * Broadcast the priority owner to everyone in the game.
     *
     * @param game the game to broadcast the priority in
     */
    public void broadcastPriority(final Game game) {
        gameNetworkingManipulator.sendMessageToPlayer(game.getPriorityPlayer(),
                ShowSingleQuestionGameMessageOutgoingMessage.builder()
                        .question("Cast spells and activate abilities.")
                        .responseOneId("PASS_PRIORITY")
                        .buttonOneText("Ok")
                        .build()
        );
        gameNetworkingManipulator.broadcastGameMessageExceptTo(game, game.getPriorityPlayer(), "Waiting for "
                + game.getActivePlayer().getName() + " to act.");

        refreshWhatCanBeCastOrActivated(game.getPriorityPlayer());
    }

    public void refreshWhatCanBeCastOrActivated(final Player player) {
        gameNetworkingManipulator.sendMessageToPlayer(player,
                RefreshCanBeCastAndActivatedListOutgoingMessage.builder()
                        .canBeCastOrActivated(player.whatCanBeActivated())
                        .build()
        );
    }

    public void resetWhatCanBeCastOrActivated(final Player player) {
        gameNetworkingManipulator.sendMessageToPlayer(player,
                RefreshCanBeCastAndActivatedListOutgoingMessage.builder()
                        .canBeCastOrActivated(Collections.emptyList())
                        .build()
        );
    }

    public Player rollUntilWinner(final Game game) {
        final Player playerOne = game.getPlayers().get(0);
        final Player playerTwo = game.getPlayers().get(1);

        final int playerOneRoll = randomCalculator.randomIntBetween(1, 10);
        final int playerTwoRoll = randomCalculator.randomIntBetween(1, 10);

        gameNetworkingManipulator.broadcastLogMessage(game, playerOne.getName() + " rolled " + playerOneRoll + ".");
        gameNetworkingManipulator.broadcastLogMessage(game, playerTwo.getName() + " rolled " + playerTwoRoll + ".");

        if (playerOneRoll == playerTwoRoll) {
            gameNetworkingManipulator.broadcastLogMessage(game, "The two rolls are equal. Rerolling!");

            return rollUntilWinner(game);
        } else if (playerOneRoll > playerTwoRoll) {
            gameNetworkingManipulator.broadcastLogMessage(game, playerOne.getName() + " will start the game.");

            return playerOne;
        } else {
            gameNetworkingManipulator.broadcastLogMessage(game, playerTwo.getName() + " will start the game.");

            return playerTwo;
        }
    }

    /**
     * Cast a land card to the player's battlefield. It will set the flag that decides if a land was cast this turn to
     * true, so after thus method was called, no more lands can be cast in the same turn.
     *
     * @param card the card that should be cast
     */
    public void castLandCardToPlayersBattlefield(final LandCard card) {
        card.getGame().setLandCastedThisTurn(true);

        /*
         * 305.1. A player who has priority may play a land card from their hand during a main phase of their turn when
         * the stack is empty. Playing a land is a special action; it doesn’t use the stack (see rule 116). Rather, the
         * player simply puts the land onto the battlefield. Since the land doesn’t go on the stack, it is never a
         * spell, and players can’t respond to it with instants or activated abilities.
         */
        castCardToPlayersBattlefieldWithoutUsingStack(card);
    }

    /**
     * Cast a card to the player's battlefield, without adding it to the stack. This is needed for some cards like lands
     * that should never be added to the stack.
     *
     * @param card the card that should be cast
     */
    public void castCardToPlayersBattlefieldWithoutUsingStack(final PermanentCard card) {
        final Player owner = card.getOwner();

        card.getGame().summonCardToBattlefield(card);

        gameNetworkingManipulator.broadcastCardEnterToBattlefield(card);

        refreshWhatCanBeCastOrActivated(owner);
    }

    /**
     * Removes the card from its owner hand and refresh the UI for everyone in the game.
     *
     * @param card the card to remove.
     */
    public void removeCardFromOwnersHand(final Card card) {
        card.getOwner().removeCardFromHand(card.getId());

        gameNetworkingManipulator.sendRemoveCardFromPlayerHand(card.getOwner(), card.getId());
        gameNetworkingManipulator.broadcastPlayerHandSize(card.getOwner());
    }
}
