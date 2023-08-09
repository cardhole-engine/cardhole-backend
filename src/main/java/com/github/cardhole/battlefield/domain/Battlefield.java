package com.github.cardhole.battlefield.domain;

import com.github.cardhole.card.domain.Card;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class Battlefield {

    private final List<Card> cards = new LinkedList<>();

    public void addCard(final Card card) {
        cards.add(card);
    }

    public Optional<Card> getCardOnBattlefield(final UUID cardId) {
        return cards.stream()
                .filter(card -> card.getId().equals(cardId))
                .findFirst();
    }
}
