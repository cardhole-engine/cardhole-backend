package com.github.cardhole.ability;

import com.github.cardhole.card.domain.Card;

public interface ActivatedAbility {

    //TODO: Input variables
    void activate();

    default boolean canBeActivated(final Card card) {
        return card.getOwner().getGame().getPriorityPlayer().equals(card.getController());
    }
}
