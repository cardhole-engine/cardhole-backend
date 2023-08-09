package com.github.cardhole.card.domain.aspect.permanent;

import com.github.cardhole.ability.ActivatedAbility;
import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.aspect.AbstractAspect;
import lombok.Getter;
import lombok.Singular;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
public class PermanentAspect extends AbstractAspect {

    @Getter
    private boolean tapped;

    @Getter
    @Singular
    private final List<ActivatedAbility> activatedAbilities;

    public boolean hasActivatedAbility() {
        return !activatedAbilities.isEmpty();
    }

    public boolean isUntapped() {
        return !tapped;
    }

    public void addActivatedAbility(final ActivatedAbility activatedAbility) {
        this.activatedAbilities.add(activatedAbility);
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
