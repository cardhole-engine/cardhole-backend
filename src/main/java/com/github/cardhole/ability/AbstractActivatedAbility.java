package com.github.cardhole.ability;

import com.github.cardhole.card.domain.Card;

import java.util.UUID;

public abstract class AbstractActivatedAbility implements ActivatedAbility {

    protected final UUID id;
    protected final Card source;

    public AbstractActivatedAbility(final Card source) {
        this.id = UUID.randomUUID();
        this.source = source;
    }

    @Override
    public UUID getId() {
        return id;
    }
}
