package com.github.cardhole.player.domain;

import com.github.cardhole.deck.domain.Deck;
import com.github.cardhole.session.domain.Session;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Player {

    private final Session session;
    private final Deck deck;

    private int life;

    public Player(final Session session, final Deck deck, final int life) {
        this.session = session;
        this.deck = deck;
        this.life = life;
    }

    public String getName() {
        return session.getName();
    }

    public int getCardCountInDeck() {
        return deck.getCardCount();
    }
}
