package com.github.cardhole.game.domain;

import com.github.cardhole.player.domain.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class Game {

    private final UUID id;
    private final String name;
    private final List<Player> players;

    @Setter
    private GameStatus status;
    @Setter
    private Player startingPlayer;

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
}
