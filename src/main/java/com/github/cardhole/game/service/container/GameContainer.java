package com.github.cardhole.game.service.container;

import com.github.cardhole.game.domain.Game;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class GameContainer {

    private final Map<UUID, Game> games = new HashMap<>();

    public void registerGame(final Game game) {
        games.put(game.getId(), game);

        //TODO: Refresh the games in the lobby for everyone there
    }

    public List<Game> listGames() {
        return new LinkedList<>(games.values());
    }

    public Optional<Game> getGame(final UUID id) {
        return Optional.ofNullable(games.get(id));
    }
}
