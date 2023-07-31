package com.github.cardhole.hand.domain;

import com.github.cardhole.card.domain.Card;

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

public class Hand {

    private final List<HandEntry> handEntries;

    public Hand() {
        handEntries = new LinkedList<>();
    }

    public void addCard(final Card card) {
        handEntries.add(new HandEntry(card));
    }

    public int getCardCount() {
        return handEntries.size();
    }

    public List<HandEntry> getCards() {
        return handEntries;
    }

    public void removeCard(final UUID cardId) {
        this.handEntries.removeIf(handEntry -> handEntry.getCard().getId().equals(cardId));
    }

    public void resetHand() {
        handEntries.clear();
    }
}
