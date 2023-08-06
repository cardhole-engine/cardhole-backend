package com.github.cardhole.card.domain;

import com.github.cardhole.card.domain.type.Subtype;
import com.github.cardhole.card.domain.type.Supertype;
import com.github.cardhole.card.domain.type.Type;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;

import java.util.Set;
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

    CardSet getSet();

    int getSetId();

    boolean canBeCast();

    void cast(Target target);

    Set<Supertype> getSupertype();
    Set<Type> getType();
    Set<Subtype> getSubtype();
}
