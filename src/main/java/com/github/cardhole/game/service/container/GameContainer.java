package com.github.cardhole.game.service.container;

import com.github.cardhole.game.domain.Game;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.List;

@Service
public class GameContainer {

    private final List<Game> games = new LinkedList<>();

    public void registerGame(final Game game) {
        games.add(game);

        //TODO: Refresh the games in the lobby for everyone there
    }

    public List<Game> listGames() {
        return games;
    }
}
