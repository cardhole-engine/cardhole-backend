package com.github.cardhole.card.domain;

import java.util.UUID;

public abstract class AbstractCard implements Card {

    private final UUID id;
    private final String name;
    private final Set set;
    private final int setId;

    public AbstractCard(final String name, final Set set, final int setId) {
        this.id = UUID.randomUUID();
        this.name = name;
        this.set = set;
        this.setId = setId;
    }

    @Override
    public UUID getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Set getSet() {
        return set;
    }

    @Override
    public int getSetId() {
        return setId;
    }
}
