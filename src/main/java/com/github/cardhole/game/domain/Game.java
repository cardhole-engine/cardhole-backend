package com.github.cardhole.game.domain;

import com.github.cardhole.player.domain.Player;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
public class Game {

    private final UUID id;
    private final String name;
    private final List<Player> players;

    private GameStatus status;

    public Game(final String name, final Player owner) {
        this.id = UUID.randomUUID();
        this.name = name;

        this.players = new ArrayList<>();
        this.players.add(owner);

        this.status = GameStatus.CREATED;
    }

    public int getPlayerCount() {
        return players.size();
    }

    public boolean hasSpaceForNewJoiner() {
        return players.size() < 2;
    }
}
