package com.github.cardhole.deck.domain;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.random.service.RandomCalculator;

import java.util.List;

public class Deck {

    private final List<DeckEntry> deckEntries;
    private final RandomCalculator randomCalculator;

    public Deck(final List<DeckEntry> deckEntries, final RandomCalculator randomCalculator) {
        this.deckEntries = deckEntries;
        this.randomCalculator = randomCalculator;
    }

    public int getCardCount() {
        return deckEntries.size();
    }

    public Class<? extends Card> drawCard() {
        return deckEntries.remove(randomCalculator.randomIntBetween(0, deckEntries.size() - 1)).getCard();
    }

    public void addCard(final Class<? extends Card> card) {
        deckEntries.add(new DeckEntry(card));
    }
}
