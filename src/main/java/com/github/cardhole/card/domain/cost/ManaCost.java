package com.github.cardhole.card.domain.cost;

import lombok.Builder;

@Builder
public class ManaCost {

    public static final ManaCost NO_COST = ManaCost.builder()
            .build();

    private final int white;
    private final int red;
    private final int blue;
    private final int black;
    private final int green;
    private final int colorless;
}
