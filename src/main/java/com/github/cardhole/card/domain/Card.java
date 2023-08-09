package com.github.cardhole.card.domain;

import com.github.cardhole.card.domain.aspect.Aspect;
import com.github.cardhole.card.domain.type.Subtype;
import com.github.cardhole.card.domain.type.Supertype;
import com.github.cardhole.card.domain.type.Type;
import com.github.cardhole.entity.domain.Entity;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.player.domain.Player;

import java.util.Set;

public interface Card extends Entity {

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

    void resolve(Target target);

    default boolean isControlledBy(Player player) {
        return getController().equals(player);
    }

    default boolean isControlledByActivePlayer() {
        return getController().equals(getGame().getActivePlayer());
    }

    Set<Supertype> getSupertype();

    Set<Type> getType();

    Set<Subtype> getSubtype();

    boolean hasAspect(Class<? extends Aspect> aspect);

    <T extends Aspect> T getAspect(Class<T> aspectClass);
}
