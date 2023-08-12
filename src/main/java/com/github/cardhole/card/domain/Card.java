package com.github.cardhole.card.domain;

import com.github.cardhole.card.domain.aspect.Aspect;
import com.github.cardhole.card.domain.type.Subtype;
import com.github.cardhole.card.domain.type.Supertype;
import com.github.cardhole.card.domain.type.Type;
import com.github.cardhole.entity.domain.Entity;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.object.domain.GameObject;
import com.github.cardhole.player.domain.Player;

import java.util.List;
import java.util.Set;

public interface Card extends GameObject {

    String getName();

    Player getOwner();

    Player getController();

    @Override
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

    /**
     * Returns the first aspect that is from the provided aspect class. If the aspect is not present on the class, then
     * an exception is thrown.
     *
     * @param aspectClass the type of the aspect to get
     * @param <T>         the type of the aspect
     * @return the aspect instance
     */
    <T extends Aspect> T getAspect(Class<T> aspectClass);

    /**
     * Returns every aspect that is from the provided aspect class. If the aspect is not present on the class, then
     * an exception is thrown.
     *
     * @param aspectClass the type of the aspect to get
     * @param <T>         the type of the aspect
     * @return the aspect instance
     */
    <T extends Aspect> List<T> getAspects(Class<T> aspectClass);

    void addAspect(final Aspect... aspects);

    <T extends Aspect> void removeAspect(Class<T> aspectClass);
}
