package com.github.cardhole.ability;

import com.github.cardhole.card.domain.permanent.PermanentCard;
import com.github.cardhole.player.domain.Player;

import java.util.UUID;

public interface ActivatedAbility {

    UUID getId();

    void activate();

    PermanentCard getSource();

    default boolean goesToStack() {
        return true;
    }

    default boolean canBeActivated() {
        return getSource().getGame().getPriorityPlayer().equals(getSource().getController()) && getSource().isUntapped();
    }
}
