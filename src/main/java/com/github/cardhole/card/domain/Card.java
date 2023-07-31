package com.github.cardhole.card.domain;

import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;

import java.util.UUID;

public interface Card {

    /**
     * Unique id for every card instance, created when the card is instantiated.
     */
    UUID getId();

    String getName();

    Player getOwner();

    Player getController();

    default Game getGame() {
        return getOwner().getGame();
    }

    Set getSet();

    int getSetId();

    boolean canBeCast();

    void cast(Target target);
}
