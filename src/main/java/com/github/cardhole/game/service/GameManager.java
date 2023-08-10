package com.github.cardhole.game.service;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.Target;
import com.github.cardhole.card.domain.aspect.creature.CreatureAspect;
import com.github.cardhole.card.domain.aspect.permanent.PermanentAspect;
import com.github.cardhole.error.domain.IllegalGameStateException;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.GameStatus;
import com.github.cardhole.game.domain.Step;
import com.github.cardhole.game.networking.GameNetworkingManipulator;
import com.github.cardhole.game.networking.cast.domain.RefreshCanBeCastAndActivatedListOutgoingMessage;
import com.github.cardhole.game.networking.combat.domain.MarkCardIsAttackingOutgoingMessage;
import com.github.cardhole.game.networking.combat.domain.MarkCardIsBlockingOutgoingMessage;
import com.github.cardhole.game.networking.message.domain.ShowDualQuestionGameMessageOutgoingMessage;
import com.github.cardhole.game.networking.message.domain.ShowSimpleGameMessageOutgoingMessage;
import com.github.cardhole.game.networking.message.domain.ShowSingleQuestionGameMessageOutgoingMessage;
import com.github.cardhole.mana.domain.Mana;
import com.github.cardhole.object.domain.GameObject;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.random.service.RandomCalculator;
import com.github.cardhole.step.damage.DamageGameStepProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GameManager {

    private final RandomCalculator randomCalculator;
    private final GameNetworkingManipulator gameNetworkingManipulator;

    //TODO: Do this somehow a little better. We don't want to have every step processor here like this.
    // Maybe collect them into a map?
    private final DamageGameStepProcessor damageGameStepProcessor;

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

        gameNetworkingManipulator.sendMessageToPlayer(winnerPlayer,
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
            /*
             * 506.1. The combat phase has five steps, which proceed in order: beginning of combat, declare
             *     attackers, declare blockers, combat damage, and end of combat. The declare blockers and combat
             *     damage steps are skipped if no creatures are declared as attackers or put onto the battlefield
             *     attacking (see rule 508.8). There are two combat damage steps if any attacking or blocking
             *     creature has first strike (see rule 702.7) or double strike (see rule 702.4).
             *
             * Skipping the step if there are no attackers.
             */
            if (!game.isAnyAttackerActive()) {
                moveToStep(game, Step.END_COMBAT);

                // The new step will take over the processing from here (incl. refreshing the UI, etc.).
                return;
            } else {
                game.setStep(Step.BLOCK);
            }
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

    public void moveToStep(final Game game, final Step step) {
        game.setStep(step);

        gameNetworkingManipulator.broadcastStepChangeMessage(game, game.getStep());

        processStep(game);
    }

    public void moveToFirstTurn(final Game game) {
        game.setTurn(1);
        game.setStep(Step.UNTAP);
        game.movePriority();

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
                game.getBattlefield().getObjects().stream()
                        .filter(Card::isControlledByActivePlayer)
                        .filter(card -> card.getAspect(PermanentAspect.class).isTapped())
                        .forEach(tappedCard -> {
                            tappedCard.getAspect(PermanentAspect.class).untap();

                            gameNetworkingManipulator.broadcastCardUntappedOnBattlefield(tappedCard);
                        });

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

                movePriority(game);
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

                movePriority(game);
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

                movePriority(game);
            }
            case BEGIN_COMBAT -> {
                movePriority(game);
            }
            case ATTACK -> {
                broadcastDeclareAttackers(game);
            }
            case BLOCK -> {
                broadcastDeclareBlockers(game);
            }
            case DAMAGE -> damageGameStepProcessor.processStep(game);
            case END_COMBAT -> {
                movePriority(game);
            }
            case POSTCOMBAT_MAIN -> {
                movePriority(game);
            }
            case END -> {
                movePriority(game);
            }
            case CLEANUP -> {
                movePriority(game);
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

        gameNetworkingManipulator.broadcastRefreshManaPool(player);
    }

    public void addManaToPlayer(final Player player, final List<Mana> mana) {
        player.getManaPool().addMana(mana);

        gameNetworkingManipulator.broadcastRefreshManaPool(player);
    }

    /**
     * Moves the priority to the next player, or move the game to the next phase if no-one is left who holds priority
     * for this phase.
     *
     * @param game the game to upgrade the priority for
     */
    public void movePriority(final Game game) {
        //TODO: add state based actions, like losing the game, etc (see: https://yawgatog.com/resources/magic-rules/#R704)

        if (game.getPriorityPlayer() != null) {
            resetWhatCanBeCastOrActivated(game.getPriorityPlayer());
        }

        game.movePriority();

        // Do not move the game into the next step, only clear the stack first
        if (game.isStackActive()) {
            broadcastPriority(game);

            refreshWhatCanBeCastOrActivated(game.getPriorityPlayer());

            return;
        }

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

    public void broadcastDeclareAttackers(final Game game) {
        gameNetworkingManipulator.sendMessageToPlayer(game.getActivePlayer(),
                ShowSingleQuestionGameMessageOutgoingMessage.builder()
                        .question("Declare attackers.")
                        .responseOneId("DECLARE_ATTACKERS")
                        .buttonOneText("Ok")
                        .build()
        );
        gameNetworkingManipulator.broadcastGameMessageExceptTo(game, game.getActivePlayer(), "Waiting for "
                + game.getActivePlayer().getName() + " to declare attackers.");

        refreshWhatCanAttack(game.getActivePlayer());

        game.setWaitingForAttackers(true);
    }

    public void refreshWhatCanAttack(final Player player) {
        gameNetworkingManipulator.sendMessageToPlayer(player,
                RefreshCanBeCastAndActivatedListOutgoingMessage.builder()
                        .canBeCastOrActivated(player.whatCanAttack())
                        .build()
        );
    }

    public void broadcastDeclareBlockers(final Game game) {
        final Player opponent = game.getPlayers().stream()
                .filter(player -> !game.isActivePlayer(player))
                .findFirst()
                .orElseThrow();

        gameNetworkingManipulator.sendMessageToPlayer(opponent,
                ShowSingleQuestionGameMessageOutgoingMessage.builder()
                        .question("Declare blockers.")
                        .responseOneId("DECLARE_BLOCKERS")
                        .buttonOneText("Ok")
                        .build()
        );
        gameNetworkingManipulator.broadcastGameMessageExceptTo(game, opponent, "Waiting for "
                + opponent.getName() + " to declare blockers.");

        refreshWhatCanBlock(opponent);

        game.setWaitingForBlockers(true);
    }

    public void refreshWhatCanBlock(final Player player) {
        gameNetworkingManipulator.sendMessageToPlayer(player,
                RefreshCanBeCastAndActivatedListOutgoingMessage.builder()
                        .canBeCastOrActivated(player.whatCanBlock())
                        .build()
        );
    }

    public void refreshWhatCanBlockedBy(final Card card) {
        gameNetworkingManipulator.sendMessageToPlayer(card.getController(),
                RefreshCanBeCastAndActivatedListOutgoingMessage.builder()
                        .canBeCastOrActivated(card.getGame().canBeBlockedBy(card))
                        .build()
        );
    }

    public void markCardAsAttacking(final Card card) {
        gameNetworkingManipulator.sendMessageToEveryone(card.getGame(),
                MarkCardIsAttackingOutgoingMessage.builder()
                        .cardId(card.getId())
                        .build()
        );
    }

    public void markCardAsDefending(final Card blocker, final Card blocked) {
        gameNetworkingManipulator.sendMessageToEveryone(blocker.getGame(),
                MarkCardIsBlockingOutgoingMessage.builder()
                        .blocker(blocker.getId())
                        .blocked(blocked.getId())
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

    public void castCard(final Card card, final Target target) {
        /*
         * We do not move the priority here.
         *
         * "The rule is: whenever a player plays a spell, that player gets priority again until he passes it. So, in
         * theory, a player could play four Lightning Bolts in a row, with a kickered Urza’s Rage on top of that if he
         * wanted to. As unfair as this might seem, it does not generally give the player any special advantage. Each
         * spell still resolves one at a time; the fact that the player played the spells one after the other does not
         * give the spells any special status. So, regardless of how many spells a player plays in a row, the other
         * player will always get a chance to respond."
         *
         * See: https://mtg-archive.fandom.com/wiki/Priority
         */
        card.cast(target);

        removeCardFromOwnersHand(card);
        refreshWhatCanBeCastOrActivated(card.getController());
    }

    public void putCardToStack(final Card card) {
        final Game game = card.getGame();

        game.getStack().enterZone(card);

        gameNetworkingManipulator.broadcastCardPutToStack(card);
    }

    public void removeCardFromStack(final Game game) {
        final GameObject gameObject = game.getStack().removeActiveEntry()
                .orElseThrow();

        if (game.isStackEmpty()) {
            game.setStackWasCleared(true);
        }

        if (gameObject instanceof Card card) {
            gameNetworkingManipulator.broadcastCardRemovedFromStack(card);

            //TODO: Targetting
            card.resolve(null);
        } else {
            //TODO: Other than cards
        }
    }

    /**
     * Cast a land card to the player's battlefield. It will set the flag that decides if a land was cast this turn to
     * true, so after thus method was called, no more lands can be cast in the same turn.
     *
     * @param card the card that should be cast
     */
    public void putLandCardToPlayersBattlefield(final Card card) {
        card.getGame().setLandCastedThisTurn(true);

        /*
         * 305.1. A player who has priority may play a land card from their hand during a main phase of their turn when
         * the stack is empty. Playing a land is a special action; it doesn’t use the stack (see rule 116). Rather, the
         * player simply puts the land onto the battlefield. Since the land doesn’t go on the stack, it is never a
         * spell, and players can’t respond to it with instants or activated abilities.
         */
        putCardToPlayersBattlefield(card);
    }

    /**
     * Cast a card to the player's battlefield, without adding it to the stack. This is needed for some cards like lands
     * that should never be added to the stack.
     *
     * @param card the card that should be cast
     */
    public void putCardToPlayersBattlefield(final Card card) {
        final Player controller = card.getController();

        card.getGame().putCardToBattlefield(card);

        gameNetworkingManipulator.broadcastCardEnterToBattlefield(card);

        refreshWhatCanBeCastOrActivated(controller);
    }

    /**
     * Removes the card from its owner hand and refresh the UI for everyone in the game.
     *
     * @param card the card to remove.
     */
    public void removeCardFromOwnersHand(final Card card) {
        card.getOwner().removeCardFromHand(card);

        gameNetworkingManipulator.sendRemoveCardFromPlayerHand(card.getOwner(), card.getId());
        gameNetworkingManipulator.broadcastPlayerHandSize(card.getOwner());
    }

    /**
     * Tap a card. Only permanent cards can be tapped, and they must be on the battlefield.
     *
     * @param card the card to tap
     */
    public void tapCard(final Card card) {
        if (!card.getGame().getBattlefield().isInZone(card)) {
            throw new IllegalStateException("Only card that is in the battlefield can be tapped!");
        }

        card.getAspect(PermanentAspect.class).tap();

        gameNetworkingManipulator.broadcastCardTapped(card);
    }

    /**
     * Handles how a creature card deals <b>combat damage</b> to the player. If the provided card is not a creature
     * card then an exception will be thrown because only creatures can do combat damage.
     *
     * @param card   the card that does the damage
     * @param player the damaged player
     * @see <a href="https://yawgatog.com/resources/magic-rules/#R510">120. Damage</a>
     * @see <a href="https://yawgatog.com/resources/magic-rules/#R1202a">120.2a. Damage may be dealt as a result of
     * combat</a>
     * @see <a href="https://yawgatog.com/resources/magic-rules/#R1203a">120.3a. Damage dealt to a player by a source
     * without infect</a>
     * @see <a href="https://yawgatog.com/resources/magic-rules/#R1204">120.4. Damage is processed in a four-part
     * sequence.</a>
     * @see <a href="https://yawgatog.com/resources/magic-rules/#R510">510. Combat Damage Step</a>
     * @see <a href="https://yawgatog.com/resources/magic-rules/#R5101a">510.1a. Each attacking creature and each
     * blocking creature assigns combat damage equal to its power</a>
     */
    public void dealCombatDamageToPlayer(final Card card, final Player player) {
        if (!card.hasAspect(CreatureAspect.class)) {
            throw new IllegalGameStateException("Only creature cards can deal combat damage!", card);
        }

        final int creaturePower = card.getAspect(CreatureAspect.class).getPower();

        /*
         * 510.1a. states that "Creatures that would assign 0 or less damage this way don't assign combat damage
         * at all.".
         */
        if (creaturePower > 0) {
            //TODO: Trigger abilities that has on-damage triggers

            playerLoseLife(player, creaturePower);
        }
    }

    /**
     * The provided player loose the provided amount of life. The player does not immediately loose the game if its life
     * reaches zero because that's a state-based action, so it is checked separately.
     *
     * @param player     the player to lose life
     * @param lifeToLose the amount of life lost
     * @see <a href="https://yawgatog.com/resources/magic-rules/#R119">119. Life</a>
     * @see <a href="https://yawgatog.com/resources/magic-rules/#R1192">119.2. Damage dealt to a player normally causes
     * that player to lose that much life</a>
     * @see <a href="https://yawgatog.com/resources/magic-rules/#R1192">119.3. If an effect causes a player to gain life
     * or lose life, that player's life total is adjusted accordingly</a>
     */
    public void playerLoseLife(final Player player, final int lifeToLose) {
        player.setLife(player.getLife() - lifeToLose);
    }

    public void playerGainLife(final Player player, final int lifeToGain) {
        //TODO
    }
}
