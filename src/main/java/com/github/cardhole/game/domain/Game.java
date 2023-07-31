package com.github.cardhole.game.domain;

import com.github.cardhole.battlefield.domain.Battlefield;
import com.github.cardhole.card.domain.Card;
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
    private List<Player> phasePriority;

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

    public Player getPriorityPlayer() {
        if (phasePriority.isEmpty()) {
            return null;
        }

        return phasePriority.get(0);
    }

    public void movePriority() {
        phasePriority.remove(0);
    }

    public boolean isStepActive(final Step... steps) {
        return Arrays.stream(steps)
                .anyMatch(step -> this.step == step);
    }

    public boolean isStackEmpty() {
        return stack.isEmpty();
    }

    public void summonCardToBattlefield(final Card card) {
        battlefield.addCard(card);
    }
}
