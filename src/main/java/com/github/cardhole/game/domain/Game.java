package com.github.cardhole.game.domain;

import com.github.cardhole.battlefield.domain.Battlefield;
import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.aspect.creature.CreatureAspect;
import com.github.cardhole.deck.domain.Deck;
import com.github.cardhole.game.service.GameManager;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.session.domain.Session;
import com.github.cardhole.stack.domain.Stack;
import com.github.cardhole.stack.domain.StackEntry;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Game {

    private final UUID id;
    private final String name;
    private final List<Player> players;
    private final Battlefield battlefield;
    private final Stack stack;

    private final List<Card> attackers;
    private final Map<Card, List<Card>> blockers;

    private final GameManager gameManager;

    @Setter
    private GameStatus status;
    @Setter
    private Player startingPlayer;
    @Setter
    private boolean startingPlayerWasDecided;

    @Setter
    private int turn;
    @Setter
    private Step step;
    @Setter
    private boolean landCastedThisTurn;
    @Setter
    private boolean stackWasCleared;

    @Setter
    private Player activePlayer;

    @Setter
    private Player priorityPlayer;

    @Setter
    private boolean waitingForAttackers;
    @Setter
    private boolean waitingForBlockers;

    public Game(final GameManager gameManager, final String name) {
        this.id = UUID.randomUUID();
        this.name = name;

        this.gameManager = gameManager;

        this.players = new CopyOnWriteArrayList<>();
        this.attackers = new LinkedList<>();
        this.blockers = new HashMap<>();
        this.battlefield = new Battlefield();
        this.stack = new Stack();

        this.status = GameStatus.CREATED;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean hasSpaceForNewJoiner() {
        return players.size() < 2;
    }

    public Player createPlayer(final Session session, final Deck deck) {
        final Player player = new Player(session, this, deck, 20);

        this.players.add(player);

        return player;
    }

    public boolean isStartingPlayer(final Player player) {
        return player.equals(startingPlayer);
    }

    public Optional<Player> getPlayerForSession(final Session session) {
        return this.players.stream()
                .filter(player -> player.getSession().equals(session))
                .findFirst();
    }

    public void movePriority() {
        /*
         * 502.4. No player receives priority during the untap step, so no spells can be cast or resolve and no
         *    abilities can be activated or resolve. Any ability that triggers during this step will be held until the
         *    next time a player would receive priority, which is usually during the upkeep step. (See rule 503,
         *    “Upkeep Step.”)
         */
        if (step == Step.UNTAP) {
            priorityPlayer = null;
        }

        final Player opponent = players.stream()
                .filter(player -> !player.equals(activePlayer))
                .findFirst()
                .orElseThrow();

        /*
         * If the last stack element was cleared from the stack, the active player might do more stuff before moving
         * to the next stage.
         *
         * For example:
         *  - Active player summon a creature.
         *  - Active player pass priority.
         *  - Opponent pass priority.
         *  - The active player should gain the priority again, without moving the game to the next step.
         */
        if (stackWasCleared) {
            stackWasCleared = false;

            priorityPlayer = activePlayer;

            return;
        }

        /*
         * If the stack is active, it should be cleared before moving to the next step.
         *
         * 117.4. If all players pass in succession (that is, if all players pass without taking any actions in between
         *    passing), the spell or ability on top of the stack resolves or, if the stack is empty, the phase or step
         *    ends.
         */
        if (isStackActive()) {
            final StackEntry stackEntry = stack.getActiveEntry()
                    .orElseThrow();

            if (stackEntry.isOpponentPassedPriority()) {
                //TODO: Let the opponent act again on next stack entry, even if he already act on it.

                gameManager.removeCardFromStack(opponent.getGame());
            } else {
                stackEntry.setOpponentPassedPriority(true);

                priorityPlayer = opponent;

                return;
            }

            movePriority();

            return;
        }

        if (priorityPlayer == null && activePlayer.getStopAtStepInMyTurn().getOrDefault(step, false)) {
            priorityPlayer = activePlayer;

            return;
        }

        if ((isActivePlayer(priorityPlayer) || priorityPlayer == null) && opponent.getStopAtStepInOpponentTurn().getOrDefault(step, false)) {
            priorityPlayer = opponent;
        } else {
            priorityPlayer = null;
        }
    }

    public boolean isStepActive(final Step... steps) {
        return Arrays.stream(steps)
                .anyMatch(step -> this.step == step);
    }

    public boolean isStackEmpty() {
        return stack.isEmpty();
    }

    public boolean isStackActive() {
        return !stack.isEmpty();
    }

    public void putCardToBattlefield(final Card card) {
        battlefield.addCard(card);
    }

    public boolean isActivePlayer(final Player player) {
        return activePlayer.equals(player);
    }

    public void moveToNextTurn() {
        turn = turn + 1;

        landCastedThisTurn = false;

        activePlayer = activePlayer.equals(players.get(0)) ? players.get(1) : players.get(0);

        step = Step.UNTAP;
    }

    public boolean isAnyAttackerActive() {
        return !attackers.isEmpty();
    }

    public void addAttacker(final Card card) {
        this.attackers.add(card);
    }

    public boolean isAttacking(final Card card) {
        return this.attackers.contains(card);
    }

    public void addBlocker(final Card blocked, final Card blocker) {
        this.blockers.computeIfAbsent(blocked, __ -> new LinkedList<>())
                .add(blocker);
    }

    public List<UUID> canBeBlockedBy(final Card card) {
        return battlefield.getCards().stream()
                .filter(cardOnBattlefield -> !cardOnBattlefield.isControlledBy(card.getController()))
                .filter(this::isAttacking)
                .filter(cardOnBattlefield -> cardOnBattlefield.hasAspect(CreatureAspect.class))
                .filter(cardOnBattlefield -> cardOnBattlefield.getAspect(CreatureAspect.class).canBeBlockedBy(card))
                .map(Card::getId)
                .toList();
    }
}
