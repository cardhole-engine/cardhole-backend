package com.github.cardhole.player.domain;

import com.github.cardhole.ability.ActivatedAbility;
import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.aspect.ability.HasActivatedAbilityAspect;
import com.github.cardhole.card.domain.aspect.permanent.PermanentAspect;
import com.github.cardhole.entity.domain.Entity;
import com.github.cardhole.game.domain.Game;
import com.github.cardhole.game.domain.Step;
import com.github.cardhole.mana.domain.ManaPool;
import com.github.cardhole.random.service.RandomCalculator;
import com.github.cardhole.session.domain.Session;
import com.github.cardhole.zone.hand.Hand;
import com.github.cardhole.zone.library.Library;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Stream;

@RequiredArgsConstructor
public class Player implements Entity {

    @Getter
    private final UUID id;

    @Getter
    private final Session session;

    private final Hand hand;

    @Getter
    private final Library library;

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

    @Getter
    private final Map<Step, Boolean> stopAtStepInMyTurn;

    @Getter
    private final Map<Step, Boolean> stopAtStepInOpponentTurn;

    public Player(final Session session, final Game game, final int life) {
        this.id = UUID.randomUUID();

        this.session = session;
        this.game = game;
        this.library = new Library(new RandomCalculator());
        this.life = life;

        this.hand = new Hand();
        this.manaPool = new ManaPool();

        this.stopAtStepInMyTurn = new EnumMap<>(Step.class);
        this.stopAtStepInMyTurn.put(Step.PRECOMBAT_MAIN, true);
        this.stopAtStepInMyTurn.put(Step.ATTACK, true);
        this.stopAtStepInMyTurn.put(Step.BLOCK, true);
        this.stopAtStepInMyTurn.put(Step.POSTCOMBAT_MAIN, true);

        this.stopAtStepInOpponentTurn = new EnumMap<>(Step.class);
        this.stopAtStepInOpponentTurn.put(Step.BLOCK, true);
    }

    public String getName() {
        return session.getName();
    }

    public int getCardCountInDeck() {
        return library.cardsInZone();
    }

    public int getCardCountInHand() {
        return hand.cardsInZone();
    }

    /**
     * Shuffles the users hand back into it's deck and returning the removed card's ids.
     *
     * @return the cards ids from the hand that were shuffled back
     */
    public List<UUID> shuffleHandBackToDeck() {
        return hand.getCards().stream()
                .map(card -> {
                    hand.leaveZone(card);

                    library.enterZone(card);

                    return card.getId();
                })
                .toList();
    }

    public List<Card> drawCards(final int amount) {
        final List<Card> drawnCards = new LinkedList<>();

        for (int i = 1; i <= amount; i++) {
            final Card card = library.drawCard();

            hand.enterZone(card);

            drawnCards.add(card);
        }

        return drawnCards;
    }

    public List<UUID> whatCanBeActivated() {
        final Stream<UUID> canBeCastedFromHand = hand.getCards().stream()
                .filter(Card::canBeCast)
                .map(Card::getId);

        final Stream<UUID> canBeActivatedOnBattlefield = game.getBattlefield().getCards().stream()
                .filter(card -> card.isControlledBy(this)
                        && card.getAspects(HasActivatedAbilityAspect.class).stream()
                        .map(HasActivatedAbilityAspect::getActivatedAbility)
                        .anyMatch(ActivatedAbility::canBeActivated)
                )
                .map(Card::getId);

        return Stream.of(canBeCastedFromHand, canBeActivatedOnBattlefield)
                .flatMap(Function.identity())
                .toList();
    }

    public List<UUID> whatCanAttack() {
        return game.getBattlefield().getCards().stream()
                .filter(card -> card.isControlledBy(this) && card.getAspect(PermanentAspect.class).isUntapped())
                .map(Card::getId)
                .toList();
    }

    public List<UUID> whatCanBlock() {
        return game.getBattlefield().getCards().stream()
                .filter(card -> card.isControlledBy(this) && card.getAspect(PermanentAspect.class).isUntapped())
                .map(Card::getId)
                .toList();
    }

    public Optional<Card> getCardInHand(final UUID cardId) {
        return hand.getCards().stream()
                .filter(card -> card.getId().equals(cardId))
                .findFirst();
    }

    public void removeCardFromHand(final Card card) {
        hand.leaveZone(card);
    }
}
