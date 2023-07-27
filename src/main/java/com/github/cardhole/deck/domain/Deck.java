package com.github.cardhole.deck.domain;

import java.util.List;

public class Deck {

    private final List<DeckEntry> deckEntries;

    public Deck(final List<DeckEntry> deckEntries) {
        this.deckEntries = deckEntries;
    }

    public int getCardCount() {
        return deckEntries.size();
    }
}
