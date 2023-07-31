package com.github.cardhole.ability;

import com.github.cardhole.player.domain.Player;

import java.util.UUID;

public abstract class AbstractActivatedAbility implements ActivatedAbility {

    protected final UUID id;

    public AbstractActivatedAbility() {
        this.id = UUID.randomUUID();
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public boolean goesToStack() {
        return true;
    }
}
