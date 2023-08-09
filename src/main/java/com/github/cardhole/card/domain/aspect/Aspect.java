package com.github.cardhole.card.domain.aspect;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.Target;
import com.github.cardhole.entity.domain.Entity;

public interface Aspect extends Entity {

    boolean isAttachableTo(Card card);

    default boolean canBeCast() {
        return true;
    }

    default void resolve(final Target target) {
    }

    default void cast(final Target target) {
    }
}
