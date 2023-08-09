package com.github.cardhole.card.domain.aspect;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.Target;

public interface Aspect {

    boolean isAttachableTo(Card card);

    default boolean canBeCast() {
        return true;
    }

    default void resolve(final Target target) {
    }

    default void cast(final Target target) {
    }
}
