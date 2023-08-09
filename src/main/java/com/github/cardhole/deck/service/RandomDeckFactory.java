package com.github.cardhole.deck.service;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.CardSet;
import com.github.cardhole.random.service.RandomCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RandomDeckFactory {

    private final RandomCalculator randomCalculator;

    //TODO: This is only temporary! We will need a card editor and such.
    public List<Class<? extends Card>> buildRandomDeck() {
        final List<Class<? extends Card>> possibleCards = Arrays.stream(CardSet.values())
                .flatMap(set -> set.getCards().values().stream())
                .toList();

        return IntStream.range(0, 60)
                .mapToObj(__ -> randomCalculator.randomEntryFromList(possibleCards))
                .collect(Collectors.toList());
    }
}
