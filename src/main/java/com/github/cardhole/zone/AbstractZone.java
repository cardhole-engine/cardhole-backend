package com.github.cardhole.zone;

import com.github.cardhole.card.domain.Card;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public abstract class AbstractZone implements Zone {

    protected final List<Card> cards = new LinkedList<>();

    public Optional<Card> getCard(final UUID cardId) {
        return cards.stream()
                .filter(card -> card.getId().equals(cardId))
                .findFirst();
    }

    //TODO: Implement enter zone and leave zone here... They should clear the cards from any effects
    @Override
    public void enterZone(final Card card) {
        cards.add(card);
    }

    @Override
    public void leaveZone(final Card card) {
        cards.remove(card);
    }

    @Override
    public boolean isInZone(final Card card) {
        return cards.contains(card);
    }
}
