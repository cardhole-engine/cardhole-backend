package com.github.cardhole.card.domain;

import java.util.UUID;

public interface Card {

    /**
     * Unique id for every card instance, created when the card is instantiated.
     */
    UUID getId();

    String getName();

    Set getSet();

    int getSetId();
}
