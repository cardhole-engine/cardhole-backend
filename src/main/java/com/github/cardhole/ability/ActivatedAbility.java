package com.github.cardhole.ability;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.aspect.permanent.PermanentAspect;
import com.github.cardhole.entity.domain.Entity;

public interface ActivatedAbility extends Entity {

    void activate();

    Card getSource();

    default boolean goesToStack() {
        return true;
    }

    default boolean canBeActivated() {
        return getSource().getGame().getPriorityPlayer().equals(getSource().getController())
                && getSource().getAspect(PermanentAspect.class).isUntapped();
    }
}
