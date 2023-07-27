package com.github.cardhole.player.domain;

import com.github.cardhole.deck.domain.Deck;
import com.github.cardhole.session.domain.Session;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@RequiredArgsConstructor
public class Player {

    @Getter
    private final UUID id;
    @Getter
    private final Session session;
    private final Deck deck;

    @Getter
    private int life;

    public Player(final Session session, final Deck deck, final int life) {
        this.id = UUID.randomUUID();
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
