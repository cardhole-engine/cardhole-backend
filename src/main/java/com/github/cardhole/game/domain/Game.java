package com.github.cardhole.game.domain;

import com.github.cardhole.battlefield.domain.Battlefield;
import com.github.cardhole.card.domain.permanent.PermanentCard;
import com.github.cardhole.deck.domain.Deck;
import com.github.cardhole.game.service.GameManager;
import com.github.cardhole.player.domain.Player;
import com.github.cardhole.session.domain.Session;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Game {

    private final UUID id;
    private final String name;
    private final List<Player> players;
    private final Queue<Object> stack;

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
    private Player activePlayer;

    @Setter
    @Getter
    private Player priorityPlayer;

    private final Battlefield battlefield; // TODO:Shouldn't have a setter

    public Game(final GameManager gameManager, final String name) {
        this.id = UUID.randomUUID();
        this.name = name;

        this.gameManager = gameManager;

        this.players = new CopyOnWriteArrayList<>();
        this.battlefield = new Battlefield();
        this.stack = new LinkedList<>();

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

        if (priorityPlayer == null && activePlayer.getStopAtStepInMyTurn().getOrDefault(step, false)) {
            priorityPlayer = activePlayer;

            return;
        }

        final Player opponent = players.stream()
                .filter(player -> !player.equals(activePlayer))
                .findFirst()
                .orElseThrow();

        if (isActivePlayer(priorityPlayer) && opponent.getStopAtStepInOpponentTurn().getOrDefault(step, false)) {
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

    public void summonCardToBattlefield(final PermanentCard card) {
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
}
