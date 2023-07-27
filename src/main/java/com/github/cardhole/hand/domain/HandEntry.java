package com.github.cardhole.hand.domain;

import com.github.cardhole.card.domain.Card;

import java.util.UUID;

public class HandEntry {

    private final UUID id;
    private final Card card;

    public HandEntry(final Card card) {
        this.id = UUID.randomUUID();
        this.card = card;
    }
}
