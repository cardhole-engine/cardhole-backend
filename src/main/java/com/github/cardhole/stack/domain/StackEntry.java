package com.github.cardhole.stack.domain;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.Target;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class StackEntry {

    private final Card card;
    private final Target target; //TODO: populate this

    @Setter

    private boolean opponentPassedPriority;
}
