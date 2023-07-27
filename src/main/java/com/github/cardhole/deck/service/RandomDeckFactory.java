package com.github.cardhole.deck.service;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.Set;
import com.github.cardhole.deck.domain.Deck;
import com.github.cardhole.deck.domain.DeckEntry;
import com.github.cardhole.random.service.RandomCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RandomDeckFactory {

    private final RandomCalculator randomCalculator;

    //TODO: This is only temporary! We will need a card editor and such.
    public Deck buildRandomDeck() {
        final List<Class<? extends Card>> possibleCards = Arrays.stream(Set.values())
                .flatMap(set -> set.getCards().values().stream())
                .toList();

        final List<DeckEntry> deckEntries = IntStream.range(0, 60)
                .mapToObj(__ -> randomCalculator.randomEntryFromList(possibleCards))
                .map(DeckEntry::new)
                .toList();

        return new Deck(deckEntries);
    }
}
