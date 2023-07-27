package com.github.cardhole.game.domain;

import com.github.cardhole.player.domain.Player;
import com.github.cardhole.session.domain.Session;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
@Setter
public class Game {

    private final UUID id;
    private final String name;
    private final List<Player> players;

    private GameStatus status;
    private Player startingPlayer;
    private Player activePlayer;

    public Game(final String name, final Player owner) {
        this.id = UUID.randomUUID();
        this.name = name;

        this.players = new CopyOnWriteArrayList<>();
        this.players.add(owner);

        this.status = GameStatus.CREATED;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean hasSpaceForNewJoiner() {
        return players.size() < 2;
    }

    public void joinPlayer(final Player player) {
        this.players.add(player);
    }

    public void resetStartingPlayer() {
        this.startingPlayer = null;
    }

    public boolean isStartingPlayer(final Player player) {
        return player.equals(startingPlayer);
    }

    public Optional<Player> getPlayerForSession(final Session session) {
        return this.players.stream()
                .filter(player -> player.getSession().equals(session))
                .findFirst();
    }
}
