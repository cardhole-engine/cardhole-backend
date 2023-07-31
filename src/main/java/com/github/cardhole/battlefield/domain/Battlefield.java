package com.github.cardhole.battlefield.domain;

import com.github.cardhole.card.domain.permanent.PermanentCard;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class Battlefield {

    private final List<PermanentCard> cards = new LinkedList<>();

    public void addCard(final PermanentCard card) {
        cards.add(card);
    }
}
