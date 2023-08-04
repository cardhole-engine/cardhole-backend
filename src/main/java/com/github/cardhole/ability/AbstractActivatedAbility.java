package com.github.cardhole.ability;

import com.github.cardhole.card.domain.permanent.PermanentCard;
import lombok.Getter;

import java.util.UUID;

public abstract class AbstractActivatedAbility implements ActivatedAbility {

    protected final UUID id;

    @Getter
    protected final PermanentCard source;

    public AbstractActivatedAbility(final PermanentCard source) {
        this.id = UUID.randomUUID();
        this.source = source;
    }

    @Override
    public UUID getId() {
        return id;
    }
}
