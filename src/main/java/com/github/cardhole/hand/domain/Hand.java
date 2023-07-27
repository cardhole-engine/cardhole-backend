package com.github.cardhole.hand.domain;

import com.github.cardhole.card.domain.Card;

import java.util.LinkedList;
import java.util.List;

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

    public void resetHand() {
        handEntries.clear();
    }
}
