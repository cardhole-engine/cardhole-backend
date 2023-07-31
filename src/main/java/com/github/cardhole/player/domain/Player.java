package com.github.cardhole.player.domain;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.deck.domain.Deck;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.hand.domain.Hand;
import com.github.cardhole.hand.domain.HandEntry;
import com.github.cardhole.mana.domain.ManaPool;
import com.github.cardhole.session.domain.Session;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class Player {

    @Getter
    private final UUID id;

    @Getter
    private final Session session;

    private final Hand hand;
    private final Deck deck;

    @Getter
    private final Game game;

    @Getter
    private final ManaPool manaPool;

    @Getter
    private int life;

    @Getter
    @Setter
    private boolean waitingForMulliganReply;

    @Getter
    @Setter
    private int mulliganCount;

    public Player(final Session session, final Game game, final Deck deck, final int life) {
        this.id = UUID.randomUUID();

        this.session = session;
        this.game = game;
        this.deck = deck;
        this.life = life;

        this.hand = new Hand();
        this.manaPool = new ManaPool();
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

    /**
     * Shuffles the users hand back into it's deck and returning the removed card's ids.
     *
     * @return the cards ids from the hand that were shuffled back
     */
    public List<UUID> shuffleHandBackToDeck() {
        final List<UUID> removedCardIds = hand.getCards().stream()
                .map(HandEntry::getCard)
                .map(card -> {
                    deck.addCard(card.getClass());

                    return card.getId();
                })
                .toList();

        hand.resetHand();

        return removedCardIds;
    }

    public List<Card> drawCards(final int amount) {
        final List<Card> drawnCards = new LinkedList<>();

        for (int i = 1; i <= amount; i++) {
            try {
                final Card card = deck.drawCard().getConstructor(Game.class, Player.class)
                        .newInstance(game, this);

                hand.addCard(card);

                drawnCards.add(card);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        return drawnCards;
    }

    public List<UUID> whatCanBeActivated() {
        final Stream<UUID> canBeCastedFromHand = hand.getCards().stream()
                .map(HandEntry::getCard)
                .filter(Card::canBeCast)
                .map(Card::getId);

        final Stream<UUID> canBeActivatedOnBattlefield = game.getBattlefield().getCards().stream()
                .filter(card -> card.getOwner() == this)
                .map(Card::getId);

        return Stream.of(canBeCastedFromHand, canBeActivatedOnBattlefield)
                .flatMap(Function.identity())
                .toList();
    }

    public Optional<Card> getCardInHand(final UUID cardId) {
        return hand.getCards().stream()
                .map(HandEntry::getCard)
                .filter(card -> card.getId().equals(cardId))
                .findFirst();
    }

    public void removeCardFromHand(final UUID cardId) {
        hand.removeCard(cardId);
    }
}
