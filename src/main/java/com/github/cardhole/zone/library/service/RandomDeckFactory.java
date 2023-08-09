package com.github.cardhole.zone.library.service;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.CardSet;
import com.github.cardhole.random.service.RandomCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RandomDeckFactory {

    private final RandomCalculator randomCalculator;

    //TODO: This is only temporary! We will need a card editor and such.
    public List<Class<? extends Card>> buildRandomDeck() {
        final List<Class<? extends Card>> possibleCards = Arrays.stream(CardSet.values())
                .flatMap(set -> set.getCards().values().stream())
                .toList();

        final List<Class<? extends Card>> result = new LinkedList<>();

        for (int i = 0; i < 60; i++) {
            result.add(randomCalculator.randomEntryFromList(possibleCards));
        }

        return result;
    }
}
