package com.github.cardhole.game.service.container;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.service.GameManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GameRegistry {

    private final GameManager gameManager;

    private final Map<UUID, Game> games = new HashMap<>();

    public Game createGame(final String name) {
        final Game game = new Game(gameManager, name);

        games.put(game.getId(), game);

        return game;
    }

    public void removeGame(final Game game) {
        games.remove(game.getId());
    }

    public List<Game> listGames() {
        return new LinkedList<>(games.values());
    }

    public Optional<Game> getGame(final UUID id) {
        return Optional.ofNullable(games.get(id));
    }
}
