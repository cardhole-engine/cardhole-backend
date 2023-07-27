package com.github.cardhole.deck.domain;


import com.github.cardhole.card.domain.Card;
import lombok.Getter;

import java.util.UUID;

@Getter
public class DeckEntry {

    private final UUID id;
    private final Class<? extends Card> card;

    public DeckEntry(final Class<? extends Card> card) {
        this.id = UUID.randomUUID();
        this.card = card;
    }
}
