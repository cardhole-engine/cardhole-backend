package com.github.cardhole.ability;

import com.github.cardhole.card.domain.permanent.PermanentCard;
import com.github.cardhole.player.domain.Player;

import java.util.UUID;

public interface ActivatedAbility {

    UUID getId();

    void activate(Player activator);

    boolean goesToStack();

    default boolean canBeActivated(final PermanentCard card) {
        return card.getGame().getPriorityPlayer().equals(card.getController()) && card.isUntapped();
    }
}
