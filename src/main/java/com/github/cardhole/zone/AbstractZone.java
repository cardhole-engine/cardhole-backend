package com.github.cardhole.zone;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.object.domain.GameObject;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public abstract class AbstractZone<T extends GameObject> implements Zone<T> {

    protected final List<T> cards = new LinkedList<>();

    public Optional<T> getCard(final UUID cardId) {
        return cards.stream()
                .filter(card -> card.getId().equals(cardId))
                .findFirst();
    }

    //TODO: Implement enter zone and leave zone here... They should clear the cards from any effects
    @Override
    public void enterZone(final T card) {
        cards.add(card);
    }

    @Override
    public void leaveZone(final T card) {
        cards.remove(card);
    }

    @Override
    public boolean isInZone(final T card) {
        return cards.contains(card);
    }

    @Override
    public int cardsInZone() {
        return cards.size();
    }
}
