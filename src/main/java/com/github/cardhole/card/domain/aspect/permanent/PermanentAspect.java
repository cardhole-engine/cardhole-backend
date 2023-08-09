package com.github.cardhole.card.domain.aspect.permanent;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.aspect.AbstractAspect;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PermanentAspect extends AbstractAspect {

    private boolean tapped;

    public boolean isUntapped() {
        return !tapped;
    }

    public void tap() {
        tapped = true;
    }

    public void untap() {
        tapped = false;
    }

    @Override
    public boolean isAttachableTo(final Card card) {
        if (card.hasAspect(PermanentAspect.class)) {
            throw new IllegalStateException("The card is already a permanent card!");
        }

        return true;
    }
}
