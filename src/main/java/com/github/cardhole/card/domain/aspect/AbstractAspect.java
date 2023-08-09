package com.github.cardhole.card.domain.aspect;

import com.github.cardhole.card.domain.Card;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
public abstract class AbstractAspect implements Aspect {

    @Getter
    protected final UUID id;
    protected final Card assignedTo;

    public AbstractAspect(final Card assignedTo) {
        this.id = UUID.randomUUID();
        this.assignedTo = assignedTo;
    }
}
