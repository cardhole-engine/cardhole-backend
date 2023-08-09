package com.github.cardhole.card.domain.aspect.creature;

import com.github.cardhole.card.domain.Card;
import com.github.cardhole.card.domain.Target;
import com.github.cardhole.card.domain.aspect.AbstractAspect;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class CreatureAspect extends AbstractAspect {

    protected int power;
    protected int toughness;

    @Override
    public void cast(final Target target) {
        assignedTo.getGame().getGameManager().putCardToStack(assignedTo);
    }

    @Override
    public void resolve(final Target target) {
        assignedTo.getGame().getGameManager().putCardToPlayersBattlefield(assignedTo);
    }

    @Override
    public boolean isAttachableTo(final Card card) {
        if (card.hasAspect(CreatureAspect.class)) {
            throw new IllegalStateException("The card is already a creature card!");
        }

        return true;
    }

    public boolean canBeBlockedBy(final Card card) {
        return true;
    }
}
