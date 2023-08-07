package com.github.cardhole.battlefield.domain;

import com.github.cardhole.card.domain.permanent.PermanentCard;
import lombok.Getter;

import java.net.CacheRequest;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Getter
public class Battlefield {

    private final List<PermanentCard> cards = new LinkedList<>();

    public void addCard(final PermanentCard card) {
        cards.add(card);
    }

    public Optional<PermanentCard> getCardOnBattlefield(final UUID cardId) {
        return cards.stream()
                .filter(card -> card.getId().equals(cardId))
                .findFirst();
    }
}
