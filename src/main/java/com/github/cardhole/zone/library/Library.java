package com.github.cardhole.zone.library;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.random.service.RandomCalculator;
import com.github.cardhole.zone.AbstractZone;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Library extends AbstractZone<Card> {

    private final RandomCalculator randomCalculator;

    public Card drawCard() {
        return cards.remove(randomCalculator.randomIntBetween(0, cards.size() - 1));
    }
}
