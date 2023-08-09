package com.github.cardhole.deck.domain;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.entity.domain.Entity;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeckEntry implements Entity {

    private final UUID id;
    private final Class<? extends Card> card;

    public DeckEntry(final Class<? extends Card> card) {
        this.id = UUID.randomUUID();
        this.card = card;
    }
}
