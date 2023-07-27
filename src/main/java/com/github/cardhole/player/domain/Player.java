package com.github.cardhole.player.domain;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.deck.domain.Deck;
import com.github.cardhole.hand.domain.Hand;
import com.github.cardhole.session.domain.Session;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class Player {

    @Getter
    private final UUID id;
    @Getter
    private final Session session;

    private final Deck deck;
    private final Hand hand;

    @Getter
    private int life;

    public Player(final Session session, final Deck deck, final int life) {
        this.id = UUID.randomUUID();
        this.session = session;
        this.deck = deck;
        this.life = life;
        this.hand = new Hand();
    }

    public String getName() {
        return session.getName();
    }

    public int getCardCountInDeck() {
        return deck.getCardCount();
    }

    public int getCardCountInHand() {
        return hand.getCardCount();
    }

    public void drawCard() {
        drawCards(1);
    }

    public List<Card> drawCards(final int amount) {
        final List<Card> drawnCards = new LinkedList<>();

        for (int i = 1; i <= amount; i++) {
            try {
                final Card card = deck.drawCard().getConstructor().newInstance();

                hand.addCard(card);

                drawnCards.add(card);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        return drawnCards;
    }
}
