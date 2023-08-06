package com.github.cardhole.card.domain;

import com.github.cardhole.card.implementation.m14.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public enum CardSet {

    M14(
            Map.ofEntries(
                    Map.entry(230, PlainsI.class),
                    Map.entry(231, PlainsII.class),
                    Map.entry(232, PlainsIII.class),
                    Map.entry(233, PlainsIV.class)
            )
    );

    @Getter
    private final Map<Integer, Class<? extends Card>> cards;
}
