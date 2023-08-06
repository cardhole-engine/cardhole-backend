package com.github.cardhole.card.domain.cost;

import lombok.Builder;

@Builder
public class ManaCost {

    private final int white;
    private final int red;
    private final int blue;
    private final int black;
    private final int green;
    private final int colorless;
}
