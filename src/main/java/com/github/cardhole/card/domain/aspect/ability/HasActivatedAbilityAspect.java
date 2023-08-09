package com.github.cardhole.card.domain.aspect.ability;

import com.github.cardhole.ability.ActivatedAbility;
import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.aspect.AbstractAspect;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class HasActivatedAbilityAspect extends AbstractAspect {

    private final ActivatedAbility activatedAbility;

    @Override
    public boolean isAttachableTo(Card card) {
        return true;
    }
}
