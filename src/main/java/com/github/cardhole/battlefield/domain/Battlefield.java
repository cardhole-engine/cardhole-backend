package com.github.cardhole.battlefield.domain;

import com.github.cardhole.card.domain.Card;
import lombok.Getter;

import java.util.LinkedList;
import java.util.List;

@Getter
public class Battlefield {

    private final List<Card> cards = new LinkedList<>();

    public void addCard(final Card card) {
        cards.add(card);
    }
}
