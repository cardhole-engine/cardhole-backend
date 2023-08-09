package com.github.cardhole.card.domain.aspect;

import com.github.cardhole.card.domain.Card;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class AbstractAspect implements Aspect {

    protected final Card assignedTo;

    public AbstractAspect(final Card assignedTo) {
        this.assignedTo = assignedTo;
    }
}
